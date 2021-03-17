package playground.stream

import cacheValues
import com.dropbox.android.external.cache3.Cache
import com.dropbox.android.external.cache3.CacheBuilder
import combine
import concatScanEager
import countErrorTransformation
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import predicateErrorTransformation
import retryWith
import zip
import java.util.concurrent.TimeUnit

class TweetsPostsRepository(
    private val infoApi: InfoApi,
    ioScheduler: Scheduler = Schedulers.io(),
    computationScheduler: Scheduler = Schedulers.computation()
) {

    /**
     * The reason for this passing of Schedulers directly is because due to the fact that this is a `val` it looks like
     * the Trampoline rule in the JUnit test is not applied properly. Maybe a race condition?
     */

    val tweetsAndPostsStream = infoApi.stream
        .subscribeOn(ioScheduler)
        .observeOn(computationScheduler)
        .share()

    private val facebookPostsObservable =
        tweetsAndPostsStream.filter { it.type == InfoType.FACEBOOK_POSTS.name }.toInfoItemToObservable()
    private val tweetsObservable =
        tweetsAndPostsStream.filter { it.type == InfoType.TWEETS.name }.toInfoItemToObservable()

    private val infoDetailsCache: Cache<Id, Single<InfoItem>> = CacheBuilder.newBuilder()
        .maximumSize(200)
        .expireAfterAccess(1, TimeUnit.MINUTES).build()

    private fun Observable<InfoItemTO>.toInfoItemToObservable() = concatMapSingle { infoDetailsCache.getIfPresent(it.id) }

    private val infoDetailsCacheLoaderFun = { id: Id ->
        infoApi.getDetails(id)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { InfoItem(id, it.title, it.date) }
            .retryWith(
                combine(
                    countErrorTransformation(3),
                    predicateErrorTransformation { throwable -> throwable is ConnectionError && throwable.errorType == ConnectionErrorType.UNKNOWN }
                )
            ).cacheValues()
    }

    /*
    Different ways of flattening an observable and transforming it back to a list
     */

    private val currentFacebookPostsFlatMapIterable: Single<List<InfoItem>> = infoApi.currentFacebookPosts
        .map { it.items }
        .toObservable()
        .flatMapIterable { it }
        .concatMapEager { itemTo ->
            itemTo.toInfoItem().toObservable()
        }.reduce(emptyList(), { t1: List<InfoItem>, t2: InfoItem ->
            t1 + t2
        })

    private val currentFacebookPostsFromIterable: Single<List<InfoItem>> = infoApi.currentFacebookPosts
        .map { it.items }
        .flatMapObservable { Observable.fromIterable(it) }
        .concatMapEager { itemTo ->
            itemTo.toInfoItem().toObservable()
        }.collect({ emptyList() }, { t1: List<InfoItem>, t2: InfoItem ->
            t1 + t2
        })

    private val currentFacebookPostsObservableFlatten: Observable<InfoItem> = infoApi.currentFacebookPosts
        .flattenAsObservable { it.items }
        .concatMapEager { itemTo ->
            itemTo.toInfoItem().toObservable()
        }

    private fun InfoItemTO.toInfoItem() =
        infoDetailsCache.get(id) { infoDetailsCacheLoaderFun(id) }

    private val currentTweetsObservableZip: Single<List<InfoItem>> = infoApi.currentTweets
        .map { it.items }
        .flatMap { itemsTo ->
            zip(itemsTo.map { infoItemTo ->
                infoItemTo.toInfoItem()
            }, defaultWhenEmpty = emptyList())
        }

    val aggregatedFacebookPostsObservableEager: Observable<List<InfoItem>> = concatScanEager(
        initialValueSingle = currentFacebookPostsObservableFlatten.toList(),
        valuesObservable = facebookPostsObservable,
        accumulator = { content, update -> content + update }
    )

    val aggregatedTweetsObservableEager: Observable<List<InfoItem>> = concatScanEager(
        initialValueSingle = currentTweetsObservableZip,
        valuesObservable = tweetsObservable,
        accumulator = { content, update -> content + update }
    )

    val aggregatedFacebookPostsObservable =
        currentFacebookPostsObservableFlatten.concatWith(facebookPostsObservable)
            .scan(emptyList<InfoItem>(), { t1, t2 -> t1 + t2 })

    val aggregatedTweetsObservable: Observable<List<InfoItem>> =
        currentTweetsObservableZip.flatMapObservable { itemList ->
            tweetsObservable.scan(itemList, { t1, t2 -> t1 + t2 })
        }

    fun getContentItemsObservable(type: InfoType): Observable<List<InfoItem>> =
        when (type) {
            InfoType.TWEETS -> infoApi.currentTweets
            InfoType.FACEBOOK_POSTS -> infoApi.currentFacebookPosts
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { infoTO -> infoTO.items.map { it.id } }
            .flattenAsObservable { it }
            .concatWith(getInfoTypeIdsObservable(type))
            .concatMapSingle { infoDetailsCache.get(it) { infoDetailsCacheLoaderFun(it) } }
            .scan(emptyList(), { t1: List<InfoItem>, t2: InfoItem -> t1 + t2 })

    private fun getInfoTypeIdsObservable(type: InfoType): Observable<Id> =
        infoApi.stream
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .filter { it.type.toContentType() == type }
            .map { it.id }

    private fun String.toContentType() = when (this) {
        InfoType.TWEETS.name -> InfoType.TWEETS
        InfoType.FACEBOOK_POSTS.name -> InfoType.FACEBOOK_POSTS
        else -> null
    }
}

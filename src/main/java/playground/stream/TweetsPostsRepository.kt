package playground.stream

import com.github.davidmoten.rx2.RetryWhen
import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.cache3.CacheLoader
import com.nytimes.android.external.cache3.LoadingCache
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import utils.cacheAtomicReference
import utils.concatScanEager
import utils.zip
import java.util.concurrent.TimeUnit

class TweetsPostsRepository(private val infoApi: InfoApi,
                            ioScheduler: Scheduler = Schedulers.io(),
                            computationScheduler: Scheduler = Schedulers.computation()) {

    /**
     * The reason for this passing of Schedulers directly is because due to the fact that this is a `val` it looks like
     * the Trampoline rule in the JUnit test is not applied properly. Maybe a race condition?
     */

    val tweetsAndPostsStream = infoApi.stream
            .subscribeOn(ioScheduler)
            .observeOn(computationScheduler)
            .share()

    private val facebookPostsObservable = tweetsAndPostsStream.filter { it.type == InfoType.FACEBOOK_POSTS.name }.toInfoItemToObservable()
    private val tweetsObservable = tweetsAndPostsStream.filter { it.type == InfoType.TWEETS.name }.toInfoItemToObservable()

    private val infoDetailsCache: LoadingCache<Id, Single<InfoItem>> = CacheBuilder.newBuilder()
            .maximumSize(200)
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build(CacheLoader.from { id ->
                infoApi.getDetails(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { InfoItem(id, it.title, it.date) }
                    .retryWhen(RetryWhen
                        .maxRetries(3)
                        .retryIf { throwable ->
                            throwable is ConnectionError && throwable.errorType == ConnectionErrorType.UNKNOWN
                        }
                        .build())
                    .cacheAtomicReference()
            })

    private fun Observable<InfoItemTO>.toInfoItemToObservable() = concatMapSingle { infoDetailsCache.get(it.id) }

    /*
    Different ways of flattening an observable and transforming it back to a list
     */

    private val currentFacebookPostsFlatMapIterable: Single<List<InfoItem>> = infoApi.currentFacebookPosts
        .map { it.items }
        .toObservable()
        .flatMapIterable { it }
        .concatMapEager { itemTo ->
            infoDetailsCache.get(itemTo.id)?.toObservable()
        }.reduce(emptyList(), { t1: List<InfoItem>, t2: InfoItem ->
            t1 + t2
        })

    private val currentFacebookPostsFromIterable: Single<List<InfoItem>> = infoApi.currentFacebookPosts
        .map { it.items }
        .flatMapObservable { Observable.fromIterable(it) }
        .concatMapEager { itemTo ->
            infoDetailsCache.get(itemTo.id)?.toObservable()
        }.collect({ emptyList() }, { t1: List<InfoItem>, t2: InfoItem ->
            t1 + t2
        })

    private val currentFacebookPostsObservableFlatten: Single<List<InfoItem>> = infoApi.currentFacebookPosts
        .map { it.items }
        .flattenAsObservable { it }
        .concatMapEager { itemTo ->
            infoDetailsCache.get(itemTo.id)?.toObservable()
        }.toList()

    private val currentTweetsObservableZip: Single<List<InfoItem>> = infoApi.currentTweets
        .map { it.items }
        .flatMap { itemsTo ->
            zip(itemsTo.map { it.id }.map { id ->
                infoDetailsCache.get(id)!!
            })
        }

    val aggregatedFacebookPostsObservableEager: Observable<List<InfoItem>> = concatScanEager(
        initialValueSingle = currentFacebookPostsObservableFlatten,
        valuesObservable = facebookPostsObservable,
        accumulator = { content, update -> content + update }
    )

    val aggregatedTweetsObservableEager: Observable<List<InfoItem>> = concatScanEager(
        initialValueSingle = currentTweetsObservableZip,
        valuesObservable = tweetsObservable,
        accumulator = { content, update -> content + update }
    )

    val aggregatedFacebookPostsObservable: Observable<List<InfoItem>> =
        currentFacebookPostsObservableFlatten.flatMapObservable { itemList ->
            facebookPostsObservable.scan(itemList, { t1, t2 -> t1 + t2 })
        }

    val aggregatedTweetsObservable: Observable<List<InfoItem>> =
        currentTweetsObservableZip.flatMapObservable { itemList ->
            tweetsObservable.scan(itemList, { t1, t2 -> t1 + t2 })
        }
}

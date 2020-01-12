package playground.stream.flow

import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.cache3.CacheLoader
import com.nytimes.android.external.cache3.LoadingCache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import playground.stream.InfoItem
import playground.stream.InfoType
import java.io.IOException
import java.util.concurrent.TimeUnit


class TweetsPostsFlowRepository(private val infoApi: TweetsPostsFlowExercise.InfoFlowApi) {

    private val tweetsAndPostsStream = infoApi.stream
        .flowOn(Dispatchers.IO)
        .retryWhen { cause, attempt -> cause is IOException || attempt < 3 }

    private val facebookPostsFlow =
        tweetsAndPostsStream.filter { it.type == InfoType.FACEBOOK_POSTS.name }.toInfoItemFlow()
    private val tweetsFlow =
        tweetsAndPostsStream.filter { it.type == InfoType.TWEETS.name }.toInfoItemFlow()

    private val infoDetailsCache: LoadingCache<ContentId, Deferred<InfoItem>> = CacheBuilder.newBuilder()
        .maximumSize(200)
        .expireAfterAccess(1, TimeUnit.MINUTES)
        .build(CacheLoader.from { id ->
            CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
                retryIO {
                    val infoItemDetailsTO = infoApi.getDetails(id).await()
                    InfoItem(id, infoItemDetailsTO.title, infoItemDetailsTO.date)
                }
            }
        })

    private fun Flow<TweetsPostsFlowExercise.InfoItemTO>.toInfoItemFlow() = flatMapConcat {
        flow {
            emit(infoDetailsCache.get(it.id)?.await())
        }
    }

    private val currentFacebookPosts =
        CoroutineScope(Dispatchers.IO).async {
            retryIO {
                val infoTO = infoApi.currentFacebookPosts.await()
                infoTO.items.map { infoItemTO ->
                    infoDetailsCache.get(infoItemTO.id)?.await()
                }.toList()
            }
        }

    private val currentTweetsObservable =
        CoroutineScope(Dispatchers.IO).async {
            retryIO {
                val infoTO = infoApi.currentFacebookPosts.await()
                infoTO.items.map { infoItemTO ->
                    infoDetailsCache.get(infoItemTO.id)?.await()
                }.toList()
            }
        }

    val aggregatedFacebookPostsObservable = flow<List<InfoItem>> {
        facebookPostsFlow.scan(currentFacebookPosts.await(), { t1, t2 -> t1 + t2 })
    }

    val aggregatedTweetsObservable = flow<List<InfoItem>> {
        tweetsFlow.scan(currentTweetsObservable.await(), { t1, t2 -> t1 + t2 })
    }
}



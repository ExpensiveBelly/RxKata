package playground.stream.flow

import com.dropbox.android.external.cache4.Cache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import playground.stream.InfoItem
import playground.stream.InfoType
import java.io.IOException
import java.util.concurrent.TimeUnit


@ExperimentalCoroutinesApi
class TweetsPostsFlowRepository(private val infoApi: TweetsPostsFlowExercise.InfoFlowApi) {

    private val tweetsAndPostsStream = infoApi.stream
        .flowOn(Dispatchers.IO)
        .retryWhen { cause, attempt -> cause is IOException || attempt < 3 }

    private val facebookPostsFlow =
        tweetsAndPostsStream.filter { it.type == InfoType.FACEBOOK_POSTS.name }.toInfoItemFlow()
    private val tweetsFlow =
        tweetsAndPostsStream.filter { it.type == InfoType.TWEETS.name }.toInfoItemFlow()

    private val infoDetailsCache: Cache<ContentId, Deferred<InfoItem>> = Cache.Builder.newBuilder()
        .maximumCacheSize(200)
        .expireAfterAccess(1, TimeUnit.MINUTES)
        .build()

    private fun Flow<TweetsPostsFlowExercise.InfoItemTO>.toInfoItemFlow() = flatMapConcat {
        flow {
            emit(it.toInfoItemDeferred().await())
        }
    }

    private fun TweetsPostsFlowExercise.InfoItemTO.toInfoItemDeferred() = infoDetailsCache.get(id) {
        CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
            retryIO {
                val infoItemDetailsTO = infoApi.getDetails(id).await()
                InfoItem(id, infoItemDetailsTO.title, infoItemDetailsTO.date)
            }
        }
    }


    private val currentFacebookPosts =
        CoroutineScope(Dispatchers.IO).async {
            retryIO {
                val infoTO = infoApi.currentFacebookPosts.await()
                infoTO.items.map { infoItemTO ->
                    infoItemTO.toInfoItemDeferred().await()
                }.toList()
            }
        }

    private val currentTweetsObservable =
        CoroutineScope(Dispatchers.IO).async {
            retryIO {
                val infoTO = infoApi.currentFacebookPosts.await()
                infoTO.items.map { infoItemTO ->
                    infoItemTO.toInfoItemDeferred().await()
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



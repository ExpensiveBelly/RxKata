package playground.stream.flow

import com.dropbox.android.external.cache3.Cache
import com.dropbox.android.external.cache3.CacheBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import playground.stream.InfoItem
import playground.stream.InfoType
import java.io.IOException
import java.util.concurrent.TimeUnit


@FlowPreview
@ExperimentalCoroutinesApi
class TweetsPostsFlowRepository(private val infoApi: TweetsPostsFlowExercise.InfoFlowApi) {

	private val tweetsAndPostsStream = infoApi.stream
		.flowOn(Dispatchers.IO)
		.retryWhen { cause, attempt -> cause is IOException || attempt < 3 }

	private val facebookPostsFlow =
		tweetsAndPostsStream.filter { it.type == InfoType.FACEBOOK_POSTS.name }.toInfoItemFlow()
	private val tweetsFlow =
		tweetsAndPostsStream.filter { it.type == InfoType.TWEETS.name }.toInfoItemFlow()

	private val infoDetailsCache: Cache<ContentId, Deferred<InfoItem>> = CacheBuilder.newBuilder()
		.maximumSize(200)
		.expireAfterAccess(1, TimeUnit.MINUTES)
		.build()

	private fun Flow<TweetsPostsFlowExercise.InfoItemTO>.toInfoItemFlow() = flatMapConcat {
		flow {
			emit(it.toInfoItemAsync().await())
		}
	}

	private fun TweetsPostsFlowExercise.InfoItemTO.toInfoItemAsync() = infoDetailsCache.get(id) {
		CoroutineScope(Dispatchers.IO).async(start = CoroutineStart.LAZY) {
			retryIO {
				val infoItemDetailsTO = infoApi.getDetails(id)
				InfoItem(id, infoItemDetailsTO.title, infoItemDetailsTO.date)
			}
		}
	}


	private val currentFacebookPosts =
		CoroutineScope(Dispatchers.IO).async {
			retryIO {
				val infoTO = infoApi.currentFacebookPosts()
				infoTO.items.map { infoItemTO ->
					infoItemTO.toInfoItemAsync().await()
				}.toList()
			}
		}

	private val currentTweetsObservable =
		CoroutineScope(Dispatchers.IO).async {
			retryIO {
				val infoTO = infoApi.currentTweets()
				infoTO.items.map { infoItemTO ->
					infoItemTO.toInfoItemAsync().await()
				}.toList()
			}
		}

	val aggregatedFacebookPostsObservable = flow<List<InfoItem>> {
		facebookPostsFlow.scan(currentFacebookPosts.await()) { t1, t2 -> t1 + t2 }
	}

	val aggregatedTweetsObservable = flow<List<InfoItem>> {
		tweetsFlow.scan(currentTweetsObservable.await()) { t1, t2 -> t1 + t2 }
	}

	suspend fun getContentItemsObservable(type: InfoType): Flow<List<InfoItem>> =
		flowOf(
			when (type) {
				InfoType.TWEETS -> infoApi.currentTweets()
				InfoType.FACEBOOK_POSTS -> infoApi.currentFacebookPosts()
			}
		).flowOn(Dispatchers.IO)
			.map { infoTO -> infoTO.items.map { it.id } }
			.flatMapConcat { it -> it.asFlow() }
			.flowOn(Dispatchers.Default)
			.onCompletion { emitAll(getInfoTypeIdsObservable(type)) }
			.flatMapConcat { id -> flowOf(infoApi.getDetails(id)).map { InfoItem(id, it.title, it.date) } }
			.scan(emptyList()) { t1: List<InfoItem>, t2: InfoItem -> t1 + t2 }

	private fun getInfoTypeIdsObservable(type: InfoType): Flow<ContentId> =
		infoApi.stream
			.flowOn(Dispatchers.IO)
			.filter { it.type.toContentType() == type }
			.map { it.id }
			.flowOn(Dispatchers.Default)

	private fun String.toContentType() = when (this) {
		InfoType.TWEETS.name -> InfoType.TWEETS
		InfoType.FACEBOOK_POSTS.name -> InfoType.FACEBOOK_POSTS
		else -> null
	}
}



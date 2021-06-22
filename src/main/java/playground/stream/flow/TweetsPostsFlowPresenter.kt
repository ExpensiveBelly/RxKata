package playground.stream.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class TweetsPostsFlowPresenter(private val repository: TweetsPostsFlowRepository) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun attach(view: TweetsPostsFlowExercise.InfoView) {
//        scope.launch {
//            view.type.flatMapLatest { infoType ->
//                if (infoType == InfoType.FACEBOOK_POSTS) repository.aggregatedFacebookPostsObservable
//                else repository.aggregatedTweetsObservable
//            }.collect { view.displayItems(it) }
//        }

        scope.launch {
            view.type.flatMapLatest { type ->
                repository.getContentItemsObservable(type)
            }.collect { view.displayItems(it) }
        }
    }

    fun detach() {
        scope.cancel()
    }
}
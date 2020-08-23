package rules

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TestSchedulerRule(
    private val ioScheduler: Scheduler = Schedulers.trampoline(),
    private val computationScheduler: Scheduler = Schedulers.trampoline(),
    private val newThreadScheduler: Scheduler = Schedulers.trampoline()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        RxJavaPlugins.setIoSchedulerHandler { ioScheduler }
        RxJavaPlugins.setComputationSchedulerHandler { computationScheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { newThreadScheduler }
    }

    override fun finished(description: Description?) {
        RxJavaPlugins.reset()
        super.finished(description)
    }
}
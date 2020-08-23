package utils

import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executor


val mainScheduler = Schedulers.from(Executor(Runnable::run))
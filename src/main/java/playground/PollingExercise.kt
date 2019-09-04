package playground

import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/**
 * How to poll data with Rx depending on a given token that we need to fetch from the server?
 *
 * - Retry 3 times when an error happens
 * - Accumulate the data in the list for every interval (do not lose the data from previous interval)
 */

typealias Username = String

typealias Token = Int

data class Data(val value: String)

class PollingExercise {

    fun poll(intervalSeconds: Long): Observable<List<Int>> = throw NotImplementedError()

    private fun getLoginToken(): Observable<Token> = Observable.just<Token>(Random.nextInt(100000))

    private fun fetchData(seed: Token) = Single.timer(200, TimeUnit.MILLISECONDS).map {
        Random.nextInt(10).takeIf { it > 3 }?.let { generateSequence(seed) { it + 1 }.take(10).toList().map { Data(it.toString()) } }
                ?: throw IllegalStateException()
    }
}
package playground.threading

import io.reactivex.Observable
import org.junit.Test

private data class User(val username: String)

class DeferVsFromCallableTest {

    @Test
    fun from_callable_should_handle_exceptions() {
        getUserObservableFromCallable(true).test().assertError(IllegalStateException::class.java)
        getUserObservableFromCallable(false).test().assertValue(User("username"))
    }

    private fun getUserObservableFromCallable(throwException: Boolean): Observable<User> {
        return Observable.fromCallable { getUserFromDb(throwException) }
    }

    /**
     * But since this is Kotlin and in Kotlin all exceptions are unchecked there's no need for try/catch here
     */
    @Test
    fun defer_needs_to_handle_the_exception_by_itself() {
        getUserObservableDefer(true).test().assertError(IllegalStateException::class.java)
        getUserObservableDefer(false).test().assertValue(User("username"))
    }

    private fun getUserObservableDefer(throwException: Boolean): Observable<User> {
        return Observable.defer { Observable.just(getUserFromDb(throwException)) }
    }

    private fun getUserFromDb(throwException: Boolean): User {
        Thread.sleep(100)
        check(!throwException)
        return User("username")
    }
}
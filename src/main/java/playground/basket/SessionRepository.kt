package playground.basket

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject


class SessionRepository(private val sessionApi: SessionApi, userName: String, password: String) {

    private val loginStateSubject = BehaviorSubject.createDefault<LoginState>(LoginState.LoggedOut)

    private val loginObservable = sessionApi.login(userName, password)
        .map { sessionTO ->
            if (sessionTO.success) LoginState.LoggedIn(sessionTO.secretKey) else LoginState.LoggedOut
        }
        .doOnSuccess { loginStateSubject.onNext(it) }
        .toObservable()
        .replay(1).refCount()

    val sessionObservable = loginStateSubject.firstOrError()
        .flatMap { loginState ->
            when (loginState) {
                is LoginState.LoggedIn -> Single.just(loginState)
                else -> loginObservable.firstOrError()
            }
        }
        .toObservable()
        .concatWith(loginStateSubject) //So you keep getting notified
        .flatMapSingle { loginState ->
            when (loginState) {
                is LoginState.LoggedIn -> Single.just(loginState.key)
                LoginState.LoggedOut -> Single.error(Exception("Logged out"))
            }
        }
        .doOnDispose {
            loginStateSubject.onNext(LoginState.LoggedOut)
            sessionApi.logout()
        }
        .replay(1).refCount()

    fun reportSessionInvalid() = loginStateSubject
        .onNext(LoginState.LoggedOut)
}

sealed class LoginState {
    data class LoggedIn(val key: String) : LoginState()

    object LoggedOut : LoginState()
}

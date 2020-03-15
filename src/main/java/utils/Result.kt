package utils

sealed class Result<out T, out R> {
    data class Success<out T>(val resp: T) : Result<T, Nothing>()
    data class ErrorResult<out R>(val error: R) : Result<Nothing, R>()
}
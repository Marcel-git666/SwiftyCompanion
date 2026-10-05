package com.example.swiftycompanion.common.utils

sealed class Either<out V, out E> {
    data class Success<out V>(val value: V) : Either<V, Nothing>()
    data class Failure<out E>(val error: E) : Either<Nothing, E>()
    inline fun onSuccess(block: (V) -> Unit): Either<V, E> {
        if (this is Success) block(value)
        return this
    }
    inline fun <NewV> map(transform: (V) -> NewV): Either<NewV, E> =
        when (this) {
            is Success -> Success(transform(value))
            is Failure -> this
        }
}
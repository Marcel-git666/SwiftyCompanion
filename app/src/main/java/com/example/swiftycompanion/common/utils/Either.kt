package com.example.swiftycompanion.common.utils

sealed class Either<out V, out E> {
    data class Success<out V>(val value: V) : Either<V, Nothing>()
    data class Failure<out E>(val error: E) : Either<Nothing, E>()
}
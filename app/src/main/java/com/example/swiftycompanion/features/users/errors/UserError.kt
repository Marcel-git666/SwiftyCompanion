package com.example.swiftycompanion.features.users.errors

sealed interface UserError {
    data object NotFound : UserError
    data object NoConnection : UserError
    data object RateLimited : UserError
    data object Unauthorized : UserError
    data object ServerUnavailable : UserError
    data class Unknown(val cause: Throwable) : UserError
}
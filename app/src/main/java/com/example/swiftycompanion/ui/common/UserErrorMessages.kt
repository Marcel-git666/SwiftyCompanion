package com.example.swiftycompanion.ui.common

import androidx.annotation.StringRes
import com.example.swiftycompanion.R
import com.example.swiftycompanion.features.users.errors.UserError

@StringRes
fun UserError.messageRes(): Int =
    when (this) {
        UserError.NotFound -> R.string.error_not_found
        UserError.NoConnection -> R.string.error_no_connection
        UserError.RateLimited -> R.string.error_rate_limited
        UserError.Unauthorized -> R.string.error_unauthorized
        UserError.ServerUnavailable -> R.string.error_server_unavailable
        is UserError.Unknown -> R.string.error_unknown
    }

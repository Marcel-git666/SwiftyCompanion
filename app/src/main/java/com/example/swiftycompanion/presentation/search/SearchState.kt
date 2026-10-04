package com.example.swiftycompanion.presentation.search

import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.errors.UserError

data class SearchState(
    val query: String = "",
    val status: Status = Status.Idle,
) {
    sealed interface Status {
        data object Idle : Status
        data object Loading : Status
        data class Error(val error: UserError) : Status
        data class Found(val user: UserDto) : Status
    }
}

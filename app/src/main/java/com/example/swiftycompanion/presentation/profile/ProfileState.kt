package com.example.swiftycompanion.presentation.profile

import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.errors.UserError

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Error(val error: UserError) : ProfileState
    data class Loaded(val user: UserDto) : ProfileState
}
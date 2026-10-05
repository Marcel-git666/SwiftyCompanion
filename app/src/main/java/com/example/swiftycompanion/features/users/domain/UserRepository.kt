package com.example.swiftycompanion.features.users.domain

import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.errors.UserError

interface UserRepository {
    suspend fun getUser(login: String): Either<UserDto, UserError>
}
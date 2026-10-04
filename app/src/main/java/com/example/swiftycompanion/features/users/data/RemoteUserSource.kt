package com.example.swiftycompanion.features.users.data

import com.example.swiftycompanion.features.users.device.models.UserDto

interface RemoteUserSource {
    suspend fun getUser(login: String): UserDto
}
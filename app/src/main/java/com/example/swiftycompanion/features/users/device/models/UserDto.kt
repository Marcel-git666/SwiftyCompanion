package com.example.swiftycompanion.features.users.device.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val login: String,
    val email: String,
    @SerialName("displayname") val displayName: String,
)

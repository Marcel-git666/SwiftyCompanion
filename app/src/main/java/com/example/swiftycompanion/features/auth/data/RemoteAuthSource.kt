package com.example.swiftycompanion.features.auth.data

import com.example.swiftycompanion.features.auth.device.models.TokenDto

interface RemoteAuthSource {
    suspend fun fetchToken(): TokenDto
}
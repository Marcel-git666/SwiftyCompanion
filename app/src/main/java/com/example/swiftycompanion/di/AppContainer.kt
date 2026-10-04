package com.example.swiftycompanion.di

import com.example.swiftycompanion.BuildConfig
import com.example.swiftycompanion.common.device.createApiHttpClient
import com.example.swiftycompanion.common.device.createHttpClient
import com.example.swiftycompanion.features.auth.data.RemoteAuthSource
import com.example.swiftycompanion.features.auth.device.ApiRemoteAuthSource
import com.example.swiftycompanion.features.users.data.RemoteUserSource
import com.example.swiftycompanion.features.users.device.ApiRemoteUserSource
import io.ktor.client.HttpClient

class AppContainer {
    private val authSource: RemoteAuthSource = ApiRemoteAuthSource(
        http = createHttpClient(),
        clientId = BuildConfig.FORTY_TWO_UID,
        clientSecret = BuildConfig.FORTY_TWO_SECRET,
    )

    private val apiHttpClient: HttpClient = createApiHttpClient(authSource)

    val userSource: RemoteUserSource = ApiRemoteUserSource(apiHttpClient)
}
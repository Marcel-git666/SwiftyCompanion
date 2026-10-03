package com.example.swiftycompanion.features.auth.device

import com.example.swiftycompanion.features.auth.data.RemoteAuthSource
import com.example.swiftycompanion.features.auth.device.models.TokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters

class ApiRemoteAuthSource(
    private val http: HttpClient,
    private val clientId: String,
    private val clientSecret: String,
) : RemoteAuthSource {

    override suspend fun fetchToken(): TokenDto =
        http.submitForm(
            url = "https://api.intra.42.fr/oauth/token",
            formParameters = parameters {
                append("grant_type", "client_credentials")
                append("client_id", clientId)
                append("client_secret", clientSecret)
            },
        ).body()
}
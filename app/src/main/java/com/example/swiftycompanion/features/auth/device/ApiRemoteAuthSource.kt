package com.example.swiftycompanion.features.auth.device

import android.util.Log
import com.example.swiftycompanion.features.auth.data.RemoteAuthSource
import com.example.swiftycompanion.features.auth.device.models.TokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ApiRemoteAuthSource(
    private val http: HttpClient,
    private val clientId: String,
    private val clientSecret: String,
) : RemoteAuthSource {

    override suspend fun fetchToken(): TokenDto {
        val token: TokenDto = http.submitForm(
            url = "https://api.intra.42.fr/oauth/token",
            formParameters = parameters {
                append("grant_type", "client_credentials")
                append("client_id", clientId)
                append("client_secret", clientSecret)
            },
        ).body()

        val receivedAt = Clock.System.now()
        val createdAt = Instant.fromEpochSeconds(token.createdAt)
        val validFor = token.expiresIn.seconds
        val expiresAt = receivedAt + validFor
        Log.i(
            TAG,
            "Token received at ${receivedAt.localTime()} (created by server at ${createdAt.localTime()}), " +
                    "valid for $validFor, expires at ${expiresAt.localTime()}",
        )
        return token
    }

    private fun Instant.localTime() =
        Instant.fromEpochSeconds(epochSeconds).toLocalDateTime(TimeZone.currentSystemDefault()).time
    private companion object {
        const val TAG = "Auth"
    }
}
package com.example.swiftycompanion.common.device

import android.util.Log
import com.example.swiftycompanion.features.auth.data.RemoteAuthSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient =
    HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
    }

fun createApiHttpClient(authSource: RemoteAuthSource): HttpClient =
    createHttpClient().config {
        defaultRequest { url("https://api.intra.42.fr/v2/") }

        install(Auth) {
            bearer {
                loadTokens {
                    Log.i("Auth", "No token in memory → fetching a new one")
                    BearerTokens(authSource.fetchToken().accessToken, null)
                }
                refreshTokens {
                    Log.i(
                        "Auth",
                        "Server returned 401 (token expired/invalid) → fetching a new one",
                    )
                    BearerTokens(authSource.fetchToken().accessToken, null)
                }
            }
        }
    }
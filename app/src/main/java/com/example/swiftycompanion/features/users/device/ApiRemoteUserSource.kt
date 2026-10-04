package com.example.swiftycompanion.features.users.device

import com.example.swiftycompanion.features.users.data.RemoteUserSource
import com.example.swiftycompanion.features.users.device.models.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

class ApiRemoteUserSource(
    private val http: HttpClient,
) : RemoteUserSource {

    override suspend fun getUser(login: String): UserDto =
        http.get {
            url { appendPathSegments("users", login, encodeSlash = true) }
        }.body()
}
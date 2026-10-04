package com.example.swiftycompanion.features.users.device

import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.features.users.data.RemoteUserSource
import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.errors.UserError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.serialization.ContentConvertException
import kotlinx.io.IOException

class ApiRemoteUserSource(
    private val http: HttpClient,
) : RemoteUserSource {
    override suspend fun getUser(login: String): Either<UserDto, UserError> =
        try {
            val user: UserDto = http.get {
                url { appendPathSegments("users", login, encodeSlash = true) }
            }.body()
            Either.Success(user)
        } catch (e: ResponseException) {
            Either.Failure(e.toUserError())
        } catch (e: IOException) {
            Either.Failure(UserError.NoConnection)
        } catch (e: ContentConvertException) {
            Either.Failure(UserError.Unknown(e))
        }

    private fun ResponseException.toUserError(): UserError =
        when (response.status.value) {
            401 -> UserError.Unauthorized
            404 -> UserError.NotFound
            429 -> UserError.RateLimited
            in 500..599 -> UserError.ServerUnavailable
            else -> UserError.Unknown(this)
        }
}
package com.example.swiftycompanion.features.users.data

import android.util.Log
import com.example.swiftycompanion.common.utils.Either
import com.example.swiftycompanion.features.users.domain.UserRepository
import com.example.swiftycompanion.features.users.domain.models.User
import com.example.swiftycompanion.features.users.errors.UserError

class UserRepositoryImpl(
    private val remoteSource: RemoteUserSource,
) : UserRepository {

    private val cache = mutableMapOf<String, User>()

    override suspend fun getUser(login: String): Either<User, UserError> {
        cache[login]?.let { cached ->
            Log.d(TAG, "Cache hit for $login")
            return Either.Success(cached)
        }
        return remoteSource.getUser(login)
            .map { it.toDomain() }
            .onSuccess { cache[login] = it }
    }

    private companion object {
        const val TAG = "UserRepository"
    }
}
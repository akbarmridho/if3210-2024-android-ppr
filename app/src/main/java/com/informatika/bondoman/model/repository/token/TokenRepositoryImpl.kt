package com.informatika.bondoman.model.repository.token

import com.informatika.bondoman.network.ApiClient
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.remote.AuthService
import retrofit2.awaitResponse
import timber.log.Timber

class TokenRepositoryImpl(private var authService: AuthService) : TokenRepository {
    override suspend fun token(token: String): Resource<Boolean> {
        try {
            val call = authService.token("Bearer $token")
            val response = call.awaitResponse()

            Timber.tag("status").d(response.code().toString())

            return if (response.isSuccessful) {
                Resource.Success(true) // Return success with data
            } else if (response.code() == 401) {
                Resource.Success(false)
            } else {
                throw Exception("Error token")
            }
        } catch (e: Throwable) {
            return Resource.Error(e) // Return error with throwable
        }
    }
}
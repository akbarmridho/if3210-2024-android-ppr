package com.informatika.bondoman.model.repository

import android.util.Log
import com.informatika.bondoman.network.ApiClient
import com.informatika.bondoman.model.Resource
import retrofit2.awaitResponse
import timber.log.Timber

class TokenRepository {
    suspend fun token(token: String): Resource<Boolean> {
        try {
            val call = ApiClient.authService.token("Bearer $token")
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
package com.informatika.bondoman.repository

import android.util.Log
import com.informatika.bondoman.network.ApiClient
import com.informatika.bondoman.network.ApiResponse
import retrofit2.awaitResponse

class TokenRepository {
    suspend fun token(token: String): ApiResponse<Boolean> {
        try {
            val call = ApiClient.authService.token(token)
            val response = call.awaitResponse()

            Log.d("status", response.code().toString())

            return if (response.isSuccessful) {
                ApiResponse.Success(true) // Return success with data
            } else if (response.code() == 401) {
                ApiResponse.Success(false)
            } else {
                throw Exception("Error token")
            }
        } catch (e: Throwable) {
            return ApiResponse.Error(Exception("Error token", e))
        }
    }
}
package com.informatika.bondoman.data.repository

import android.util.Log
import com.informatika.bondoman.api.ApiClient
import com.informatika.bondoman.utils.ApiResponse
import retrofit2.awaitResponse

class TokenRepository {
    suspend fun token(token: String): ApiResponse<Boolean> {
        try {
            val call = ApiClient.authService.token(token)
            val response = call.awaitResponse()

            Log.d("status", response.code().toString())

            if (response.isSuccessful) {
                return ApiResponse.Success(true) // Return success with data
            } else {
                throw Exception("Error token")
            }
        } catch (e: Throwable) {
            return ApiResponse.Error(Exception("Error token", e))
        }
    }
}
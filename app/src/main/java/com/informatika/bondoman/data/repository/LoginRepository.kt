package com.informatika.bondoman.data.repository

import com.informatika.bondoman.api.ApiClient
import com.informatika.bondoman.utils.ApiResponse
import com.informatika.bondoman.data.model.LoginRequest
import retrofit2.awaitResponse
import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */


class LoginRepository {
    suspend fun login(username: String, password: String): ApiResponse<String> {
        try {
            val call = ApiClient.authService.login(LoginRequest(username, password))
            val response = call.awaitResponse()

            if (response.isSuccessful) {
                val token = response.body()?.token
                if (token != null) {
                    return ApiResponse.Success(token) // Return success with data
                } else {
                    throw IOException("Error logging in", Throwable("Token is invalid"))
                }
            } else {
                throw IOException("Error logging in", Throwable(response.errorBody()?.string()))
            }
        } catch (e: Throwable) {
            return ApiResponse.Error(IOException("Error logging in", e))
        }
    }

}
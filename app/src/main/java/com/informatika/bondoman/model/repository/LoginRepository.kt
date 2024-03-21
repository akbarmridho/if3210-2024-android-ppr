package com.informatika.bondoman.model.repository

import com.informatika.bondoman.model.remote.request.LoginRequest
import com.informatika.bondoman.network.ApiClient
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.remote.AuthService
import retrofit2.awaitResponse
import java.io.IOException

class LoginRepository constructor(private val authService: AuthService) {
    suspend fun login(username: String, password: String): Resource<String> {
        try {
            val call = authService.login(LoginRequest(username, password))
            val response = call.awaitResponse()

            if (response.isSuccessful) {
                val token = response.body()?.token
                if (token != null) {
                    return Resource.Success(token) // Return success with data
                } else {
                    throw IOException("Error logging in", Throwable("Token is invalid"))
                }
            } else {
                throw IOException("Error logging in", Throwable(response.errorBody()?.string()))
            }
        } catch (e: Throwable) {
            return Resource.Error(e) // Return error with throwable
        }
    }

}
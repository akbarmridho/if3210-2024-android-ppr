package com.informatika.bondoman.model.repository.login

import com.informatika.bondoman.model.remote.request.LoginRequest
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.remote.service.AuthService
import retrofit2.awaitResponse
import java.io.IOException

class LoginRepositoryImpl constructor(private var authService: AuthService) : LoginRepository {
    override suspend fun login(username: String, password: String): Resource<String> {
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
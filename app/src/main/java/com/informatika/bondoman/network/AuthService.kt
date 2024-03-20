package com.informatika.bondoman.model.remote

import com.informatika.bondoman.model.remote.request.LoginRequest
import com.informatika.bondoman.model.remote.response.LoginResponse
import com.informatika.bondoman.model.remote.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/login")
    fun login(@Body loginUser: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/token")
    fun token(@Header("Authorization") token: String): Call<TokenResponse>
}

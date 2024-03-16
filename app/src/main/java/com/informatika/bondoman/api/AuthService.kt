package com.informatika.bondoman.api

import com.informatika.bondoman.data.model.LoginRequest
import com.informatika.bondoman.data.model.LoginResponse
import com.informatika.bondoman.data.model.TokenResponse
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

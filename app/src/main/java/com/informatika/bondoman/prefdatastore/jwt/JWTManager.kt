package com.informatika.bondoman.prefdatastore.jwt

import kotlinx.coroutines.flow.Flow

interface JWTManager {
    fun isAuthenticated(): Flow<Boolean>

    suspend fun getToken(): String

    suspend fun saveToken(token: String)

    suspend fun onLogout()

}
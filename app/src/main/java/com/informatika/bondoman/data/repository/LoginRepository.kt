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

    // in-memory cache of the loggedInUser object
    var user: Boolean? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        user = null
    }

    fun logout() {
        // TODO: revoke authentication
        user = null
    }

    suspend fun login(username: String, password: String): ApiResponse<Boolean> {
        try {
            val call = ApiClient.authService.login(LoginRequest(username, password))
            val response = call.awaitResponse()

            if (response.isSuccessful) {
                val token = response.body()?.token
                if (token != null) {
                    setLoggedInUser(true)
                    return ApiResponse.Success(true) // Return success with data
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

    public fun setLoggedInUser(isUserValid: Boolean) {
        this.user = isUserValid
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
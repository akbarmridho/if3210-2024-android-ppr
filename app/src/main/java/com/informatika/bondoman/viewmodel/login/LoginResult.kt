package com.informatika.bondoman.viewmodel.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val jwtToken: String? = null,
    val error: Int? = null
)
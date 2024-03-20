package com.informatika.bondoman.view.activity.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val jwtToken: String? = null,
    val error: Int? = null
)
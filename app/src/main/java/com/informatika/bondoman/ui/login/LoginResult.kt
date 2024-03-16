package com.informatika.bondoman.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val error: Int? = null
)
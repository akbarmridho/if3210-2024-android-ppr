package com.informatika.bondoman.model.remote.response

import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "token")
    val token: String
)

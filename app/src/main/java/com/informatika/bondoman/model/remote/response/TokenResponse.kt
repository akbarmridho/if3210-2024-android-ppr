package com.informatika.bondoman.model.remote.response

import com.squareup.moshi.Json

data class TokenResponse(
    @Json(name = "nim")
    val nim: String,
    @Json(name = "iat")
    val iat: Long,
    @Json(name = "exp")
    val exp: Long
)

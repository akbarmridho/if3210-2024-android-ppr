package com.informatika.bondoman.model.remote.response

import com.squareup.moshi.Json

data class UploadBillResponse(
    @Json(name = "items")
    val items: Items,
)

data class Items(
    @Json(name = "items")
    val items: List<Item>
)

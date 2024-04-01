package com.informatika.bondoman.model.remote.response

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    @Json(name = "name")
    val name: String,
    @Json(name = "qty")
    val qty: Int,
    @Json(name = "price")
    val price: Double
) : Parcelable

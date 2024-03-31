package com.informatika.bondoman.model.local.entity.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val lat: Double,
    val lon: Double,
    val adminArea: String
) : Parcelable

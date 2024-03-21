package com.informatika.bondoman.model.local.entity.transaction

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinates(
    @ColumnInfo(defaultValue = "NULL")
    val latitude: Double,
    val longitude: Double
): Parcelable

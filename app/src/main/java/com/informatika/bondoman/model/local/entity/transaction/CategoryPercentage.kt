package com.informatika.bondoman.model.local.entity.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryPercentage (
    val category : Category,
    val percentage : Float
) : Parcelable
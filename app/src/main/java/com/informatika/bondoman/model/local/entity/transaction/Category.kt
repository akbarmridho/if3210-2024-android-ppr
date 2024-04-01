package com.informatika.bondoman.model.local.entity.transaction

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class Category(val s: String) : Parcelable {
    INCOME("Income"),
    EXPENSE("Expense"),
    LOADER("")
}
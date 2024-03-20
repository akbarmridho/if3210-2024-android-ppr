package com.informatika.bondoman.model.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.informatika.bondoman.model.local.DBConstants
import kotlinx.parcelize.Parcelize

enum class Category(s: String) {
    PEMASUKAN("Pemasukan"),
    PENGELUARAN("Pengeluaran")
}

@Parcelize
data class Coordinates(
    @ColumnInfo(defaultValue = "NULL")
    val latitude: Double,
    val longitude: Double
): Parcelable

@Entity(tableName = DBConstants.mTableTransaction)
@Parcelize
data class Transaction (
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: String,

    val title: String,

    val category: Category,

    val amount: Int,

    @Embedded
    val location: Coordinates,
) : Parcelable
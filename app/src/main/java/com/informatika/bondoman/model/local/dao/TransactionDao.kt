package com.informatika.bondoman.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.informatika.bondoman.model.local.DBConstants
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.model.local.entity.transaction.Transaction


@Dao
interface TransactionDao {
    @Query("SELECT * FROM `" + DBConstants.mTableTransaction + "` WHERE _id = :id")
    fun get(id: Int): Transaction

    @Query("SELECT * FROM `" + DBConstants.mTableTransaction + "` ORDER BY createdAt DESC")
    fun getAll(): List<Transaction>

    @Query("SELECT category, SUM(amount) * 100.0 / (SELECT SUM(amount) FROM `" + DBConstants.mTableTransaction + "` ) AS percentage FROM `" + DBConstants.mTableTransaction + "` GROUP BY category")
    fun getCategoryPercentages(): List<CategoryPercentage>

    @Query("INSERT INTO `" + DBConstants.mTableTransaction + "` (title, category, amount, location_lat, location_lon, location_adminArea) VALUES(:title, :category, :amount, :location_lat, :location_lon, :location_adminArea)")
    fun insert(
        title: String,
        category: Category,
        amount: Double,
        location_lat: Double,
        location_lon: Double,
        location_adminArea: String
    )

    @Query("INSERT INTO `" + DBConstants.mTableTransaction + "` (title, category, amount) VALUES(:title, :category, :amount)")
    fun insert(title: String, category: Category, amount: Double)

    @Query("UPDATE `" + DBConstants.mTableTransaction + "` SET title = :title, amount = :amount, location_lat = :location_lat, location_lon = :location_lon, location_adminArea = :location_adminArea WHERE _id = :id")
    fun update(
        id: Int,
        title: String,
        amount: Double,
        location_lat: Double,
        location_lon: Double,
        location_adminArea: String
    )

    @Query("UPDATE `" + DBConstants.mTableTransaction + "` SET title = :title, amount = :amount WHERE _id = :id")
    fun update(id: Int, title: String, amount: Double)

    @Delete
    fun delete(transaction: Transaction)
}

package com.informatika.bondoman.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.informatika.bondoman.model.local.DBConstants
import com.informatika.bondoman.model.local.entity.Category
import com.informatika.bondoman.model.local.entity.Coordinates
import com.informatika.bondoman.model.local.entity.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM " + DBConstants.mTableTransaction)
    suspend fun getAll(): List<Transaction>

    @Query("INSERT INTO " + DBConstants.mTableTransaction + " (title, category, amount) VALUES(:title, :category, :amount, :coordinates)")
    suspend fun insert(title: String, category: Category, amount: Int, coordinates: Coordinates)

    @Query("INSERT INTO " + DBConstants.mTableTransaction + " (title, category, amount) VALUES(:title, :category, :amount)")
    suspend fun insert(title: String, category: Category, amount: Int)

    @Query("UPDATE " + DBConstants.mTableTransaction + " SET title = :title, category = :category, coordinates = :coordinates")
    suspend fun update(title: String, category: Category, coordinates: Coordinates)

    @Query("UPDATE " + DBConstants.mTableTransaction + " SET title = :title, category = :category")
    suspend fun update(title: String, category: Category)

    @Delete
    suspend fun delete(transaction: Transaction)
}

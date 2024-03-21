package com.informatika.bondoman.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.informatika.bondoman.model.local.DBConstants
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `" + DBConstants.mTableTransaction + "` WHERE _id = :id")
    suspend fun get(id: Int): Transaction

    @Query("SELECT * FROM `" + DBConstants.mTableTransaction + "` ORDER BY createdAt DESC")
    suspend fun getAll(): List<Transaction>

    @Query("INSERT INTO `" + DBConstants.mTableTransaction + "` (title, category, amount, location) VALUES(:title, :category, :amount, :location)")
    suspend fun insert(title: String, category: Category, amount: Int, location: String)

    @Query("INSERT INTO `" + DBConstants.mTableTransaction + "` (title, category, amount) VALUES(:title, :category, :amount)")
    suspend fun insert(title: String, category: Category, amount: Int)

    @Query("UPDATE `" + DBConstants.mTableTransaction + "` SET title = :title, amount = :amount, location = :location")
    suspend fun update(title: String, amount: Int, location: String)

    @Query("UPDATE `" + DBConstants.mTableTransaction + "` SET title = :title, amount = :amount")
    suspend fun update(title: String, amount: Int)

    @Delete
    suspend fun delete(transaction: Transaction)
}

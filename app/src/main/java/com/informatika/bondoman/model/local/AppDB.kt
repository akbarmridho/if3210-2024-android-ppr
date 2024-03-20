package com.informatika.bondoman.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.informatika.bondoman.model.local.dao.TransactionDao
import com.informatika.bondoman.model.local.entity.Transaction

@Database(entities = [Transaction::class], version = DBConstants.mVersion)
abstract class AppDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
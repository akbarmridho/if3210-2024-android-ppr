package com.informatika.bondoman.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.informatika.bondoman.model.local.AppDatabase
import com.informatika.bondoman.model.local.dao.TransactionDao
import com.informatika.bondoman.model.local.entity.transaction.Category
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LargeTest
class TransactionTest {
    private lateinit var transactionDao: TransactionDao
    private lateinit var db: AppDatabase
    private lateinit var context: Context

    @Before
    fun createDB() {
        context = ApplicationProvider.getApplicationContext()
        // in-memory database
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = db.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() = db.close()

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieve() {
        transactionDao.insert(title = "Test1", category = Category.PEMASUKAN, amount = 1000, location = "Jakarta")
        transactionDao.insert(title = "Test2", category = Category.PENGELUARAN, amount = 2000,)
        val transactions = transactionDao.getAll()
        assert(transactions.size == 2)
    }
}
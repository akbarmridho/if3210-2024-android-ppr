package com.informatika.bondoman.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.informatika.bondoman.model.local.AppDatabase
import com.informatika.bondoman.model.local.dao.TransactionDao
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.Location
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
        val location: Location = Location(1.0,1.0, "Jakarta")
        transactionDao.insert(title = "Test1", category = Category.INCOME, amount = 1000, locLatitude = location.lat, locLongitude = location.lon, locAdminArea = location.adminArea)
        transactionDao.insert(title = "Test2", category = Category.EXPENSE, amount = 2000,)
        val transactions = transactionDao.getAll()
        assert(transactions.size == 2)

        val lastId = transactions.last()._id
        val transaction = transactionDao.get(lastId)
        assert(transaction.title == "Test2")
    }

    @Test
    @Throws(Exception::class)
    fun update() {
        val location1: Location = Location(1.0,1.0, "Jakarta")
        transactionDao.insert(title = "Test1", category = Category.INCOME, amount = 1000, locLatitude = location1.lat, locLongitude = location1.lon, locAdminArea = location1.adminArea)
        val transactions = transactionDao.getAll()
        val lastId = transactions.last()._id
        val location2: Location = Location(2.0,2.0, "Bandung")
        transactionDao.update(lastId,"Test2", 2000, location2.lat, location2.lon, location2.adminArea)
        val transaction = transactionDao.get(lastId)
        assert(transaction.title == "Test2")
        assert(transaction.amount == 2000)
        assert(Location(location2.lat, location2.lon, location2.adminArea) == Location(transaction.locLatitude!!, transaction.locLongitude!!, transaction.locAdminArea!!))
    }

    @Test
    @Throws(Exception::class)
    fun delete() {
        val location: Location = Location(1.0,1.0, "Jakarta")
        transactionDao.insert(title = "Test1", category = Category.INCOME, amount = 1000, locLatitude = location.lat, locLongitude = location.lon, locAdminArea = location.adminArea)
        val transactions = transactionDao.getAll()
        val lastId = transactions.last()._id
        transactionDao.delete(transactions.last())
        val transaction = transactionDao.get(lastId)
        assert(transaction == null)
    }
}
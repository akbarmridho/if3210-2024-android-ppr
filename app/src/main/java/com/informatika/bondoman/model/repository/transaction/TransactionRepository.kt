package com.informatika.bondoman.model.repository.transaction

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.model.local.entity.transaction.Location
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.remote.response.Item
import java.io.File

interface TransactionRepository {
    val _listTransactionLiveData: MutableLiveData<Resource<List<Transaction>>>
    val listTransactionLiveData: LiveData<Resource<List<Transaction>>>
        get() = listTransactionLiveData

    val _transactionLiveData: MutableLiveData<Resource<Transaction>>
    val transactionLiveData: LiveData<Resource<Transaction>>
        get() = transactionLiveData

    val _categoryPercentageLiveData: MutableLiveData<Resource<List<CategoryPercentage>>>
    val categoryPercentageLiveData: LiveData<Resource<List<CategoryPercentage>>>
        get() = categoryPercentageLiveData

    suspend fun getTransaction(id: Int)
    suspend fun getAllTransaction()
    suspend fun getCategoryPercentage()
    suspend fun insertTransaction(title: String, category: Category, amount: Int, location: Location)
    suspend fun insertTransaction(title: String, category: Category, amount: Int)
    suspend fun updateTransaction(_id:Int, title: String, amount: Int, location: Location)
    suspend fun updateTransaction(_id:Int, title: String, amount: Int)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun uploadBill(token: String, imageUri: File): Resource<List<Item>>
}
package com.informatika.bondoman.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.dao.TransactionDao
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.local.entity.transaction.Transaction

class TransactionRepository(private var transactionDao: TransactionDao) {
    private val _listTransactionLiveData = MutableLiveData<Resource<List<Transaction>>>()
    private val listTransactionLiveData : LiveData<Resource<List<Transaction>>> = _listTransactionLiveData

    private val _transactionLiveData = MutableLiveData<Resource<Transaction>>()
    private val transactionLiveData : LiveData<Resource<Transaction>> = _transactionLiveData

    suspend fun getTransaction(id: Int) {
        _transactionLiveData.postValue(Resource.Loading())
        try {
            val transaction = transactionDao.get(id)
            _transactionLiveData.postValue(Resource.Success(transaction))
        } catch (e: Exception) {
            _transactionLiveData.postValue(Resource.Error(e))
        }
    }

    suspend fun getAllTransaction() {
        _listTransactionLiveData.postValue(Resource.Loading())
        try {
            val transactionList = transactionDao.getAll()
            _listTransactionLiveData.postValue(Resource.Success(transactionList))
        } catch (e: Exception) {
            _listTransactionLiveData.postValue(Resource.Error(e))
        }
    }

    suspend fun insertTransaction(title: String, category: Category, amount: Int, location: String) {
        transactionDao.insert(title, category, amount, location)
    }

    suspend fun insertTransaction(title: String, category: Category, amount: Int) {
        transactionDao.insert(title, category, amount)
    }

    suspend fun updateTransaction(title: String, amount: Int, location: String) {
        transactionDao.update(title, amount, location)
    }

    suspend fun updateTransaction(title: String, amount: Int) {
        transactionDao.update(title, amount)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun refreshTransaction() {
        _listTransactionLiveData.postValue(Resource.Loading())
        try {
            val transactionList = transactionDao.getAll()
            _listTransactionLiveData.postValue(Resource.Success(transactionList))
        } catch (e: Exception) {
            _listTransactionLiveData.postValue(Resource.Error(e))
        }
    }

    fun getListTransactionLiveData() = listTransactionLiveData

    fun getTransactionLiveData() = transactionLiveData
}
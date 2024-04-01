package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailTransactionViewModel(
    private var transactionRepository: TransactionRepository,
    val transaction: Transaction
) : ViewModel() {
    private val transactionLiveData = MutableLiveData<Resource<Transaction>>()
    private val observer = androidx.lifecycle.Observer<Resource<Transaction>> {
        transactionLiveData.postValue(it)
    }
    val _transactionLiveData: LiveData<Resource<Transaction>> = transactionLiveData

    init {
        transactionRepository.transactionLiveData.observeForever(observer)
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.getTransaction(transaction._id)
        }
    }

    fun getTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.getTransaction(transaction._id)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    override fun onCleared() {
        super.onCleared()
        transactionRepository.transactionLiveData.removeObserver(observer)
    }
}
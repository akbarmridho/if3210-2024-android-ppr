package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailTransactionViewModel(private var transactionRepository: TransactionRepository, transaction: Transaction) : ViewModel() {
    val transactionLiveData = MutableLiveData<Resource<Transaction>>()
    private val observer = androidx.lifecycle.Observer<Resource<Transaction>> {
        transactionLiveData.postValue(it)
    }

    init {
        transactionRepository.transactionLiveData.observeForever(observer)
        viewModelScope.launch {
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
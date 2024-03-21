package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import com.informatika.bondoman.viewmodel.transaction.helper.TransactionFormState
import kotlinx.coroutines.launch

class UpdateTransactionViewModel(private var transactionRepository: TransactionRepository, _id: Int) : ViewModel() {
    val transactionLiveData = MutableLiveData<Resource<Transaction>>()
    private val observer = androidx.lifecycle.Observer<Resource<Transaction>> {
        transactionLiveData.postValue(it)
    }

    // TODO: Implement updateTransaction function
    private val _updateTransactionForm = MutableLiveData<TransactionFormState>()

    init {
        transactionRepository.transactionLiveData.observeForever(observer)
        viewModelScope.launch {
            transactionRepository.getTransaction(_id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        transactionRepository.transactionLiveData.removeObserver(observer)
    }
}
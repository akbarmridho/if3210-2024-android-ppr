package com.informatika.bondoman.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportViewModel(private var transactionRepository: TransactionRepository) : ViewModel() {
    val transactionAggregation = MutableLiveData<Resource<List<CategoryPercentage>>>()
    private val observer = Observer<Resource<List<CategoryPercentage>>> {
        transactionAggregation.postValue(it)
    }

    init {
        transactionRepository.categoryPercentageLiveData.observeForever(observer)
    }

    fun getTransactionAggregation() {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.getCategoryPercentage()
        }
    }

    override fun onCleared() {
        super.onCleared()
        transactionRepository.categoryPercentageLiveData.removeObserver(observer)
    }
}
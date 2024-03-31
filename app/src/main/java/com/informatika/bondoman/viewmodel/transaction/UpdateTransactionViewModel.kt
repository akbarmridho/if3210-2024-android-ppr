package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.R
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Location
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import com.informatika.bondoman.viewmodel.transaction.helper.TransactionFormState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateTransactionViewModel(private var transactionRepository: TransactionRepository, val transaction: Transaction) : ViewModel() {
    private val _updateTransactionForm = MutableLiveData<TransactionFormState>()
    val updateTransactionFormState: LiveData<TransactionFormState> = _updateTransactionForm

    val transactionLiveData = MutableLiveData<Resource<Transaction>>()

    private val observer = androidx.lifecycle.Observer<Resource<Transaction>> {
        transactionLiveData.postValue(it)
    }

    init {
        transactionRepository.transactionLiveData.observeForever(observer)
    }


    fun updateTransaction(
        title: String,
        amount: Int,
        location: Location? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (location != null) {
                transactionRepository.updateTransaction(transaction._id, title, amount, location)
            } else {
                transactionRepository.updateTransaction(transaction._id, title, amount)
            }
        }
    }

    fun updateTransactionDataChanged(
        title: String,
        amount: String,
    ) {
        if (!isTitleValid(title)) {
            _updateTransactionForm.value = TransactionFormState(titleError = R.string.invalid_title)
        } else if (isAmountValid(amount) > 0) {
            val amountError = when (isAmountValid(amount)) {
                1 -> R.string.invalid_amount_empty
                2 -> R.string.invalid_amount_negative
                else -> null
            }
            _updateTransactionForm.value = TransactionFormState(amountError = amountError)
        } else {
            _updateTransactionForm.value = TransactionFormState(isDataValid = true)
        }
    }

    private fun isTitleValid(title: String): Boolean {
        return title.isNotBlank()
    }

    private fun isAmountValid(amount: String): Int {
        return if (amount.isBlank()) {
            1
        } else if (amount.toInt() <= 0) {
            2
        } else {
            0
        }
    }
}
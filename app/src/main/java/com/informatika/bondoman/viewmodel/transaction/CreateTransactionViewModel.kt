package com.informatika.bondoman.viewmodel.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.R
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.model.repository.TransactionRepository
import com.informatika.bondoman.viewmodel.transaction.helper.TransactionFormState
import kotlinx.coroutines.launch

class CreateTransactionViewModel(private var transactionRepository: TransactionRepository) : ViewModel() {
    private val _createTransactionForm = MutableLiveData<TransactionFormState>()
    val createTransactionFormState: LiveData<TransactionFormState> = _createTransactionForm

    fun createTransaction(
        title: String,
        category: Category,
        amount: Int,
        location: String?
    ) {
        viewModelScope.launch {
            if (location != null) {
                transactionRepository.insertTransaction(title, category, amount, location)
            } else {
                transactionRepository.insertTransaction(title, category, amount)
            }
        }
    }

    fun createTransactionDataChanged(title: String, amount: String, category: String) {
        if (!isTitleValid(title)) {
            _createTransactionForm.value = TransactionFormState(titleError = R.string.invalid_title)
        } else if (isAmountValid(amount) > 0) {
            val amountError = when (isAmountValid(amount)) {
                1 -> R.string.invalid_amount_empty
                2 -> R.string.invalid_amount_negative
                else -> null
            }
            _createTransactionForm.value = TransactionFormState(amountError = amountError)
        } else if (!isCategoryValid(category)) {
            _createTransactionForm.value = TransactionFormState(categoryError = R.string.invalid_category)
        } else {
            _createTransactionForm.value = TransactionFormState(isDataValid = true)
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

    private fun isCategoryValid(category: String = ""): Boolean {
        return category.isNotBlank()
    }
}
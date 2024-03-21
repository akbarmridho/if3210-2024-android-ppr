package com.informatika.bondoman.viewmodel.transaction.helper

data class TransactionFormState (
    val titleError: Int? = null,
    val amountError: Int? = null,
    val categoryError: Int? = null,
    val isDataValid: Boolean = false
)
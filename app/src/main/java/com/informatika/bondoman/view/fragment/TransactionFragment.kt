package com.informatika.bondoman.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.informatika.bondoman.R
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.ListTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.core.parameter.parametersOf

class TransactionFragment : Fragment() {
    val _id: Int = 0
    // TODO: delete unused viewModels just dummy
    private val listTransactionViewModel: ListTransactionViewModel by viewModel()
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModel { parametersOf(_id) }
    private val updateTransactionViewModel: UpdateTransactionViewModel by viewModel { parametersOf(_id) }
    private val createTransactionViewModel: CreateTransactionViewModel by viewModel()

    companion object {
        fun newInstance() = TransactionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }
}
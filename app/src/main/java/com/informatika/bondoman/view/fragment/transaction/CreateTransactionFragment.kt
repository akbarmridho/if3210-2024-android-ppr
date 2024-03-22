package com.informatika.bondoman.view.fragment.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.informatika.bondoman.R
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateTransactionFragment : Fragment() {
    private val createTransactionViewModel: CreateTransactionViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_transaction, container, false)
    }

    companion object {
        fun newInstance() = CreateTransactionFragment()
    }
}
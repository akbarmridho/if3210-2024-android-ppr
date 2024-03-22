package com.informatika.bondoman.view.fragment.transaction

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.UpdateTransactionFragmentBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.view.fragment.transaction.DetailTransactionFragment.Companion.ARG_TRANSACTION
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class UpdateTransactionFragment : Fragment() {

    lateinit var mUpdateTransactionFragmentBinding: UpdateTransactionFragmentBinding
    private var transaction: Transaction? = null
    private val updateTransactionViewModel: UpdateTransactionViewModel by viewModel {
        parametersOf(transaction?._id ?: -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (it.getParcelable(DetailTransactionFragment.ARG_TRANSACTION, Transaction::class.java) as? Transaction)?.let {
                    transaction = it
                }
            } else {
                (it.getParcelable(DetailTransactionFragment.ARG_TRANSACTION) as? Transaction)?.let {
                    transaction = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUpdateTransactionFragmentBinding = UpdateTransactionFragmentBinding.inflate(inflater, container, false)
        return mUpdateTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO: Implement onViewCreated
    }

    companion object {
        fun newInstance(transaction: Transaction) : UpdateTransactionFragment {
            val updateTransactionFragment = UpdateTransactionFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TRANSACTION, transaction)
            updateTransactionFragment.arguments = bundle
            return updateTransactionFragment
        }
    }
}
package com.informatika.bondoman.view.fragment.transaction

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.UpdateTransactionFragmentBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.view.fragment.transaction.DetailTransactionFragment.Companion.ARG_TRANSACTION
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


class UpdateTransactionFragment : Fragment() {

    lateinit var mUpdateTransactionFragmentBinding: UpdateTransactionFragmentBinding
    private lateinit var transaction: Transaction;
    private val updateTransactionViewModel: UpdateTransactionViewModel by viewModel {
        parametersOf(transaction)
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
        mUpdateTransactionFragmentBinding.transaction = transaction

        val etTransactionTitle = mUpdateTransactionFragmentBinding.etTransactionTitle
        val tvTransactionCategory = mUpdateTransactionFragmentBinding.tvTransactionCategory
        val etTransactionAmount = mUpdateTransactionFragmentBinding.etTransactionAmount
        val btnUpdateTransaction = mUpdateTransactionFragmentBinding.btnUpdateTransaction

        updateTransactionViewModel.updateTransactionFormState.observe(viewLifecycleOwner, Observer {
            val createTransactionState = it ?: return@Observer

            btnUpdateTransaction.isEnabled = createTransactionState.isDataValid

            if (createTransactionState.titleError != null) {
                etTransactionTitle.error = getString(createTransactionState.titleError)
                Timber.d("Title error: ${createTransactionState.titleError}")
            }

            if (createTransactionState.amountError != null) {
                etTransactionAmount.error = getString(createTransactionState.amountError)
                Timber.d("Amount error: ${createTransactionState.amountError}")
            }
        })

        etTransactionTitle.doAfterTextChanged {
            updateTransactionViewModel.updateTransactionDataChanged(
                etTransactionTitle.text.toString(),
                etTransactionAmount.text.toString(),
            )
        }

        etTransactionAmount.doAfterTextChanged {
            updateTransactionViewModel.updateTransactionDataChanged(
                etTransactionTitle.text.toString(),
                etTransactionAmount.text.toString(),
            )
        }

        btnUpdateTransaction.setOnClickListener {
            updateTransactionViewModel.updateTransaction(
                etTransactionTitle.text.toString(),
                etTransactionAmount.text.toString().toInt()
            )
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, ListTransactionFragment.newInstance())
                .commit()
            Toast.makeText(context, "Transaction updated", Toast.LENGTH_SHORT).show()
        }

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
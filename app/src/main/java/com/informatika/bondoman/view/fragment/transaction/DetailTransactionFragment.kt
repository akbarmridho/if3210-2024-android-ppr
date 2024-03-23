package com.informatika.bondoman.view.fragment.transaction

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.informatika.bondoman.DetailTransactionFragmentBinding
import com.informatika.bondoman.R
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.view.activity.MainActivity.Companion.updateTransactionFragmentTag
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DetailTransactionFragment : Fragment() {

    lateinit var mDetailTransactionFragmentBinding: DetailTransactionFragmentBinding
    private lateinit var transaction: Transaction;
    private val detatilTransactionViewModel: DetailTransactionViewModel by viewModel {
        parametersOf(transaction)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (it.getParcelable(ARG_TRANSACTION, Transaction::class.java) as? Transaction)?.let {
                    transaction = it
                }
            } else {
                (it.getParcelable(ARG_TRANSACTION) as? Transaction)?.let {
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
        mDetailTransactionFragmentBinding = DetailTransactionFragmentBinding.inflate(inflater, container, false)
        return mDetailTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDetailTransactionFragmentBinding.transaction = transaction

        val ibClose: ImageButton = mDetailTransactionFragmentBinding.ibClose
        val ibDelete: ImageButton = mDetailTransactionFragmentBinding.ibDelete
        val ibEdit: ImageButton = mDetailTransactionFragmentBinding.ibEdit

        ibClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        ibDelete.setOnClickListener {
            detatilTransactionViewModel.deleteTransaction(transaction)
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, ListTransactionFragment.newInstance())
                .commit()
        }

        ibEdit.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, UpdateTransactionFragment.newInstance(transaction!!))
                .addToBackStack(updateTransactionFragmentTag)
                .commit()
        }
    }

    companion object {
        const val ARG_TRANSACTION = "Transaction"
        fun newInstance(transaction: Transaction): DetailTransactionFragment {
            val detailTransactionFragment = DetailTransactionFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TRANSACTION, transaction)
            detailTransactionFragment.arguments = bundle
            return detailTransactionFragment
        }
    }
}
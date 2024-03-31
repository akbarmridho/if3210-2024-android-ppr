package com.informatika.bondoman.view.fragment.transaction

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.informatika.bondoman.DetailTransactionFragmentBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.view.activity.MainActivity.Companion.updateTransactionFragmentTag
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailTransactionFragment : Fragment() {

    lateinit var mDetailTransactionFragmentBinding: DetailTransactionFragmentBinding
    private lateinit var transaction: Transaction;
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModel {
        parametersOf(transaction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_TRANSACTION, Transaction::class.java)?.let {
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
        mDetailTransactionFragmentBinding =
            DetailTransactionFragmentBinding.inflate(inflater, container, false)
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
            detailTransactionViewModel.deleteTransaction(transaction)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(com.informatika.bondoman.R.id.main_activity_container, ListTransactionFragment())
                .commit()
            Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT).show()
        }

        ibEdit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(com.informatika.bondoman.R.id.main_activity_container, UpdateTransactionFragment.newInstance(transaction))
                .addToBackStack(updateTransactionFragmentTag)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        detailTransactionViewModel.getTransaction()
        detailTransactionViewModel._transactionLiveData.observe(this) {
            val _transaction = it ?: return@observe
            when (_transaction) {
                is Resource.Success -> {
                    transaction = _transaction.data
                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mDetailTransactionFragmentBinding.transaction = _transaction.data
                    }
                    mainHandler.post(runnable)
                }
                else -> {
                    // Do nothing
                }
            }
        }
    }

    companion object {
        const val ARG_TRANSACTION = "transaction"
        fun newInstance(transaction: Transaction): DetailTransactionFragment {
            val detailTransactionFragment = DetailTransactionFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TRANSACTION, transaction)
            detailTransactionFragment.arguments = bundle
            return detailTransactionFragment
        }
    }
}
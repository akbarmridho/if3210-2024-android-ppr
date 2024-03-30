package com.informatika.bondoman.view.activity.transaction

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.informatika.bondoman.DetailTransactionActivityBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DetailTransactionActivity : AppCompatActivity() {

    lateinit var mDetailTransactionFragmentBinding: DetailTransactionActivityBinding
    private lateinit var transaction: Transaction;
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModel {
        parametersOf(transaction)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailTransactionFragmentBinding = DetailTransactionActivityBinding.inflate(layoutInflater)
        setContentView(mDetailTransactionFragmentBinding.root)

        intent.extras?.let {
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

        mDetailTransactionFragmentBinding.transaction = transaction

        val ibClose: ImageButton = mDetailTransactionFragmentBinding.ibClose
        val ibDelete: ImageButton = mDetailTransactionFragmentBinding.ibDelete
        val ibEdit: ImageButton = mDetailTransactionFragmentBinding.ibEdit

        ibClose.setOnClickListener {
            finish()
        }

        ibDelete.setOnClickListener {
            detailTransactionViewModel.deleteTransaction(transaction)
            Toast.makeText(this@DetailTransactionActivity, "Transaction Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }

        ibEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(ARG_TRANSACTION, transaction)
            val intent = Intent(this, UpdateTransactionActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
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
    }
}
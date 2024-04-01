package com.informatika.bondoman.view.fragment.transaction

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.informatika.bondoman.DetailTransactionFragmentBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailTransactionFragment : Fragment() {

    private lateinit var mDetailTransactionFragmentBinding: DetailTransactionFragmentBinding
    private lateinit var transaction: Transaction
    private val detailTransactionViewModel: DetailTransactionViewModel by viewModel {
        parametersOf(transaction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safeArgs: DetailTransactionFragmentArgs by navArgs()
        transaction = safeArgs.transaction
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDetailTransactionFragmentBinding =
            DetailTransactionFragmentBinding.inflate(inflater, container, false)
        return mDetailTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDetailTransactionFragmentBinding.transaction = transaction

        val clTransactionLocation: ConstraintLayout =
            mDetailTransactionFragmentBinding.clTransactionLocation

        val ibDelete: ImageButton = mDetailTransactionFragmentBinding.ibDelete
        val ibEdit: ImageButton = mDetailTransactionFragmentBinding.ibEdit

        clTransactionLocation.setOnClickListener {
            if (transaction.location != null) {
                val location = transaction.location
                val uri = "http://maps.google.com/maps?q=loc:${location?.lat},${location?.lon}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Location is not available", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        ibDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes") { _, _ ->
                    detailTransactionViewModel.deleteTransaction(transaction)
                    findNavController().navigateUp()
                    Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            dialog.show()
        }

        ibEdit.setOnClickListener {
            val action =
                DetailTransactionFragmentDirections.actionNavigationDetailTransactionToNavigationUpdateTransaction(
                    transaction
                )
            findNavController().navigate(action)
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
package com.informatika.bondoman.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.informatika.bondoman.databinding.ListTransactionFragmentBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.view.activity.transaction.CreateTransactionActivity
import com.informatika.bondoman.view.adapter.TransactionRecyclerAdapter
import com.informatika.bondoman.viewmodel.transaction.ListTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListTransactionFragment : Fragment() {

    lateinit var mListTransactionFragmentBinding: ListTransactionFragmentBinding
    private val listTransactionViewModel: ListTransactionViewModel by viewModel()
    private lateinit var transactionRecyclerAdapter: TransactionRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListTransactionFragmentBinding = ListTransactionFragmentBinding.inflate(inflater, container, false)
        return mListTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListTransactionFragmentBinding.fabAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), CreateTransactionActivity::class.java)
            startActivity(intent)
        }

        initViews()
        listenToViewModel()
        fetchTransactions()
    }

    override fun onResume() {
        super.onResume()
        fetchTransactions()
    }

    private fun fetchTransactions() {
        listTransactionViewModel.getAllTransaction()
    }

    private fun listenToViewModel() {
        listTransactionViewModel.listTransactionLiveData.observe(viewLifecycleOwner, Observer { resource ->
            resource?.let {
                when (it) {
                    is Resource.Success -> {
                        transactionRecyclerAdapter.removeLoader()
                        transactionRecyclerAdapter.setTransactionList(ArrayList(it.data))
                    }
                    is Resource.Error ->
                        transactionRecyclerAdapter.removeLoader()
                    is Resource.Loading ->
                        transactionRecyclerAdapter.addLoader()
                }
                displayTransactions()
            }
        })
    }

    private fun initViews() {
        transactionRecyclerAdapter = TransactionRecyclerAdapter(requireContext())
        mListTransactionFragmentBinding.rvTransactions.adapter = transactionRecyclerAdapter

        val animator = mListTransactionFragmentBinding.rvTransactions.itemAnimator
        if (animator is androidx.recyclerview.widget.SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        if (!transactionRecyclerAdapter.isLoading()) {
            transactionRecyclerAdapter.addLoader()
        }
    }

    private fun displayTransactions() {
        if (transactionRecyclerAdapter.itemCount == 0) {
            mListTransactionFragmentBinding.rvTransactions.visibility = View.GONE
            mListTransactionFragmentBinding.tvEmpty.visibility = View.VISIBLE
        } else {
            mListTransactionFragmentBinding.rvTransactions.visibility = View.VISIBLE
            mListTransactionFragmentBinding.tvEmpty.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance() = ListTransactionFragment()
    }
}

package com.informatika.bondoman.view.fragment.transaction

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.informatika.bondoman.databinding.FragmentCreateTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class CreateTransactionFragment : Fragment(), AdapterView.OnItemClickListener {
    private val createTransactionViewModel: CreateTransactionViewModel by viewModel()
    private lateinit var createTransactionFragmentBinding: FragmentCreateTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        createTransactionFragmentBinding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        return createTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTransactionTitle = createTransactionFragmentBinding.etTransactionTitle
        val spTransactionCategory = createTransactionFragmentBinding.spTransactionCategory
        val etTransactionAmount = createTransactionFragmentBinding.etTransactionAmount
        val btnCreateTransaction = createTransactionFragmentBinding.btnCreateTransaction

        // add category to spinner
        val categories: MutableList<String> = ArrayList()
        for (category in Category.values()) {
            if (category != Category.LOADER) {
                categories.add(category.name)
            }
        }
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spTransactionCategory.adapter = dataAdapter

        createTransactionViewModel.createTransactionFormState.observe(viewLifecycleOwner, Observer {
            val createTransactionState = it ?: return@Observer

            btnCreateTransaction.isEnabled = createTransactionState.isDataValid

            if (createTransactionState.titleError != null) {
                etTransactionTitle.error = getString(createTransactionState.titleError)
                Timber.d("Title error: ${createTransactionState.titleError}")
            }

            if (createTransactionState.amountError != null) {
                etTransactionAmount.error = getString(createTransactionState.amountError)
                Timber.d("Amount error: ${createTransactionState.amountError}")
            }

            if (createTransactionState.categoryError != null) {
                Timber.d("Category error: ${createTransactionState.categoryError}")
                val errorText = spTransactionCategory.selectedView as TextView
                errorText.error = getString(com.informatika.bondoman.R.string.invalid_category)
                errorText.requestFocus()
            }
        })

        etTransactionTitle.doAfterTextChanged {
            createTransactionViewModel.createTransactionDataChanged(
                etTransactionTitle.text.toString(),
                etTransactionAmount.text.toString(),
                spTransactionCategory.selectedItem.toString()
            )
        }

        etTransactionAmount.doAfterTextChanged {
            createTransactionViewModel.createTransactionDataChanged(
                etTransactionTitle.text.toString(),
                etTransactionAmount.text.toString(),
                spTransactionCategory.selectedItem.toString()
            )
        }

        btnCreateTransaction.setOnClickListener {
            // TODO: add location
            createTransactionViewModel.createTransaction(
                etTransactionTitle.text.toString(),
                Category.valueOf(spTransactionCategory.selectedItem.toString()),
                etTransactionAmount.text.toString().toInt()
            )
            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(context, "Transaction created", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        createTransactionViewModel.createTransactionDataChanged(
            createTransactionFragmentBinding.etTransactionTitle.text.toString(),
            createTransactionFragmentBinding.etTransactionAmount.text.toString(),
            createTransactionFragmentBinding.spTransactionCategory.selectedItem.toString()
        )
    }

    companion object {
        fun newInstance() = CreateTransactionFragment()
    }
}
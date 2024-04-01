package com.informatika.bondoman.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.FragmentReportBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.viewmodel.ReportViewModel

class ReportFragment : Fragment() {

    private lateinit var mReportFragmentBinding: FragmentReportBinding
    private val reportViewModel: ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportViewModel.transactionAggregation.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it) {
                    is Resource.Success -> {
                        displayChart(it.data)
                    }

                    is Resource.Error ->
                        displayErrorText()

                    is Resource.Loading ->
                        displayLoadingText()
                }
            }
        }

        reportViewModel.getTransactionAggregation()
    }

    private fun displayErrorText() {
        mReportFragmentBinding.chartWrapper.visibility = View.GONE
        mReportFragmentBinding.reportLoadingText.visibility = View.GONE
        mReportFragmentBinding.reportErrorText.visibility = View.VISIBLE
    }

    private fun displayLoadingText() {
        mReportFragmentBinding.chartWrapper.visibility = View.VISIBLE
        mReportFragmentBinding.reportLoadingText.visibility = View.VISIBLE
        mReportFragmentBinding.reportErrorText.visibility = View.GONE
    }

    private fun displayChart(data: List<CategoryPercentage>) {
        mReportFragmentBinding.chartWrapper.visibility = View.VISIBLE
    }
}
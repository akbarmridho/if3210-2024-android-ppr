package com.informatika.bondoman.view.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.FragmentReportBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.local.entity.transaction.CategoryPercentage
import com.informatika.bondoman.viewmodel.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class ReportFragment : Fragment() {
    private lateinit var mReportFragmentBinding: FragmentReportBinding
    private val reportViewModel: ReportViewModel by viewModel()
    private val colors = arrayListOf((R.color.red), R.color.green)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mReportFragmentBinding = FragmentReportBinding.inflate(inflater, container, false)
        return mReportFragmentBinding.root
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

        val pieChart = mReportFragmentBinding.pieChart

        pieChart.apply {
            this.setUsePercentValues(true)
            this.getDescription().setEnabled(false)
            this.setExtraOffsets(5f, 10f, 5f, 5f)
            this.setDrawCenterText(true)
            this.setRotationAngle(0f)
            this.setRotationEnabled(true)
            this.setHighlightPerTapEnabled(true)
            this.animateY(1400, Easing.EaseInOutQuad)
            this.legend.isEnabled = false
            this.setEntryLabelColor(Color.BLACK)
            this.setEntryLabelTextSize(12f)
            this.setNoDataText("No aggregation data!")
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
        mReportFragmentBinding.reportLoadingText.visibility = View.GONE
        mReportFragmentBinding.reportErrorText.visibility = View.GONE

        val dataSet = PieDataSet(data.map { categoryPercentage -> PieEntry(categoryPercentage.percentage) }, "Transactions").apply {
            this.setDrawIcons(false);
            this.sliceSpace = 3f;
            this.iconsOffset = MPPointF(0f, 40f)
            this.selectionShift = 5f

        }
        dataSet.colors = this.colors.map { color -> resources.getColor(color) }

        val pieData = PieData(dataSet)

        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(15f)
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD)
        pieData.setValueTextColor(Color.BLACK)

        val pieChart = mReportFragmentBinding.pieChart
        pieChart.data = pieData

        pieChart.highlightValues(null)
        pieChart.invalidate()

    }
}
package com.informatika.bondoman.view.fragment.transaction

import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.UpdateTransactionFragmentBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.util.LocationUtil
import com.informatika.bondoman.view.fragment.transaction.DetailTransactionFragment.Companion.ARG_TRANSACTION
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.io.IOException
import java.util.Locale

class UpdateTransactionFragment : Fragment() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    lateinit var mUpdateTransactionActivityBinding: UpdateTransactionFragmentBinding
    private lateinit var transaction: Transaction;
    private val updateTransactionViewModel: UpdateTransactionViewModel by viewModel {
        parametersOf(transaction)
    }

    private var locationUpdated: Boolean = false

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
        mUpdateTransactionActivityBinding =
            UpdateTransactionFragmentBinding.inflate(inflater, container, false)
        return mUpdateTransactionActivityBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mUpdateTransactionActivityBinding.transaction = transaction

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val etTransactionTitle = mUpdateTransactionActivityBinding.etTransactionTitle
        val tvTransactionCategory = mUpdateTransactionActivityBinding.tvTransactionCategory
        val etTransactionAmount = mUpdateTransactionActivityBinding.etTransactionAmount
        val tvTransactionLocation = mUpdateTransactionActivityBinding.tvTransactionLocation
        val btnUpdateTransaction = mUpdateTransactionActivityBinding.btnUpdateTransaction

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
                etTransactionAmount.text.toString().toInt(),
                if (locationUpdated) tvTransactionLocation.text.toString() else null
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, DetailTransactionFragment.newInstance(transaction))
                .commit()
            Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (LocationUtil.checkLocationPermission(requireContext())) {
            if (LocationUtil.isLocationEnabled(requireContext())) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if(location == null) {
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 0
                            fastestInterval = 0
                            numUpdates = 1
                        }
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                    } else {
                        getCityName(location.latitude, location.longitude)
                    }
                }
            } else {
                Timber.d("Location not enabled")
            }
        } else {
            Timber.d("Location permission not granted")
        }

    }

    fun getCityName(lat: Double, lon: Double) {
        var cityName: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                if (list.size != 0) {
                    cityName = list[0].adminArea
                    locationUpdated = true
                    Timber.d("getCityName: $cityName")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mUpdateTransactionActivityBinding.tvTransactionLocation.text = cityName
                        mUpdateTransactionActivityBinding.tvTransactionLocation.visibility = View.VISIBLE
                    }
                    mainHandler.post(runnable)
                }
            }
        } else {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    cityName = list[0].adminArea
                    locationUpdated = true
                    Timber.d("getCityName: $cityName")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mUpdateTransactionActivityBinding.tvTransactionLocation.text = cityName
                        mUpdateTransactionActivityBinding.tvTransactionLocation.visibility = View.VISIBLE
                    }
                    mainHandler.post(runnable)

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            getCityName(lastLocation.latitude, lastLocation.longitude)
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
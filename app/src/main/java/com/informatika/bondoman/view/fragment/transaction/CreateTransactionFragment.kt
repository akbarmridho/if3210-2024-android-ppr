package com.informatika.bondoman.view.fragment.transaction

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.informatika.bondoman.databinding.FragmentCreateTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.util.LocationUtil
import com.informatika.bondoman.view.activity.BROADCAST_TRANSACTION
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.util.Locale


class CreateTransactionFragment : Fragment(), AdapterView.OnItemClickListener {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var location: com.informatika.bondoman.model.local.entity.transaction.Location? = null

    lateinit var randomizeTransactionReceiver: BroadcastReceiver

    private val createTransactionViewModel: CreateTransactionViewModel by viewModel()
    private lateinit var mCreateTransactionFragmentBinding: FragmentCreateTransactionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mCreateTransactionFragmentBinding =
            FragmentCreateTransactionBinding.inflate(inflater, container, false)
        return mCreateTransactionFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val etTransactionTitle = mCreateTransactionFragmentBinding.etTransactionTitle
        val spTransactionCategory = mCreateTransactionFragmentBinding.spTransactionCategory
        val etTransactionAmount = mCreateTransactionFragmentBinding.etTransactionAmount
        val tvTransactionLocation = mCreateTransactionFragmentBinding.tvTransactionLocation
        val btnCreateTransaction = mCreateTransactionFragmentBinding.btnCreateTransaction

        randomizeTransactionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val amount = intent?.getDoubleExtra("amount", 0.0)
                etTransactionAmount.setText(amount.toString())
            }
        }
        IntentFilter(BROADCAST_TRANSACTION).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().registerReceiver(
                    randomizeTransactionReceiver,
                    it,
                    RECEIVER_EXPORTED
                )
            } else {
                requireActivity().registerReceiver(randomizeTransactionReceiver, it)
            }
        }

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
            createTransactionViewModel.createTransaction(
                etTransactionTitle.text.toString(),
                Category.valueOf(spTransactionCategory.selectedItem.toString()),
                etTransactionAmount.text.toString().toDouble(),
                location
            )
            requireActivity().supportFragmentManager.popBackStack()
            Toast.makeText(context, "Transaction created", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (LocationUtil.checkLocationPermission(requireContext())) {
            if (LocationUtil.isLocationEnabled(requireContext())) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 0
                            fastestInterval = 0
                            numUpdates = 1
                        }
                        fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(requireContext())
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                        )
                    } else {
                        getCityName(location.latitude, location.longitude)
                        Timber.d("Location: ${location.latitude}, ${location.longitude}")
                    }
                }
            } else {
                Timber.d("Location not enabled")
            }
        } else {
            Timber.d("Location permission not granted")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(randomizeTransactionReceiver)
    }

    private fun getCityName(lat: Double, lon: Double) {
        Timber.d("getCityName: $lat, $lon")
        var adminArea: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                if (list.size != 0) {
                    adminArea = list[0].adminArea
                    location = com.informatika.bondoman.model.local.entity.transaction.Location(
                        lat,
                        lon,
                        adminArea!!
                    )
                    Timber.d("getAdminArea: $adminArea")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mCreateTransactionFragmentBinding.tvTransactionLocation.text = adminArea
                        mCreateTransactionFragmentBinding.tvTransactionLocation.visibility =
                            View.VISIBLE
                    }
                    mainHandler.post(runnable)
                }
            }
        } else {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    adminArea = list[0].adminArea
                    location = com.informatika.bondoman.model.local.entity.transaction.Location(
                        lat,
                        lon,
                        adminArea!!
                    )
                    Timber.d("getAdminArea: $adminArea")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mCreateTransactionFragmentBinding.tvTransactionLocation.text = adminArea
                        mCreateTransactionFragmentBinding.tvTransactionLocation.visibility =
                            View.VISIBLE
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        createTransactionViewModel.createTransactionDataChanged(
            mCreateTransactionFragmentBinding.etTransactionTitle.text.toString(),
            mCreateTransactionFragmentBinding.etTransactionAmount.text.toString(),
            mCreateTransactionFragmentBinding.spTransactionCategory.selectedItem.toString()
        )
    }

    companion object {
        fun newInstance() = CreateTransactionFragment()
    }
}
package com.informatika.bondoman.view.activity.transaction

import android.R
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.informatika.bondoman.databinding.ActivityCreateTransactionBinding
import com.informatika.bondoman.model.local.entity.transaction.Category
import com.informatika.bondoman.util.LocationUtil
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.util.Locale


class CreateTransactionActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private val createTransactionViewModel: CreateTransactionViewModel by viewModel()
    private lateinit var mCreateTransactionFragmentBinding: ActivityCreateTransactionBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        mCreateTransactionFragmentBinding = ActivityCreateTransactionBinding.inflate(layoutInflater)
        setContentView(mCreateTransactionFragmentBinding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@CreateTransactionActivity)

        val etTransactionTitle = mCreateTransactionFragmentBinding.etTransactionTitle
        val spTransactionCategory = mCreateTransactionFragmentBinding.spTransactionCategory
        val etTransactionAmount = mCreateTransactionFragmentBinding.etTransactionAmount
        val tvTransactionLocation = mCreateTransactionFragmentBinding.tvTransactionLocation
        val btnCreateTransaction = mCreateTransactionFragmentBinding.btnCreateTransaction

        // add category to spinner
        val categories: MutableList<String> = ArrayList()
        for (category in Category.values()) {
            if (category != Category.LOADER) {
                categories.add(category.name)
            }
        }
        val dataAdapter = ArrayAdapter(this@CreateTransactionActivity, R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spTransactionCategory.adapter = dataAdapter

        createTransactionViewModel.createTransactionFormState.observe(this@CreateTransactionActivity, Observer {
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
                etTransactionAmount.text.toString().toInt(),
                if (tvTransactionLocation.visibility == View.VISIBLE) tvTransactionLocation.text.toString() else null
            )
            finish()
            Toast.makeText(this@CreateTransactionActivity, "Transaction created", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (LocationUtil.checkLocationPermission(this)) {
            if (LocationUtil.isLocationEnabled(this@CreateTransactionActivity)) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if(location == null) {
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 0
                            fastestInterval = 0
                            numUpdates = 1
                        }
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@CreateTransactionActivity)
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
        val geocoder = Geocoder(this@CreateTransactionActivity, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                if (list.size != 0) {
                    cityName = list[0].adminArea
                    Timber.d("getCityName: $cityName")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mCreateTransactionFragmentBinding.tvTransactionLocation.text = cityName
                        mCreateTransactionFragmentBinding.tvTransactionLocation.visibility = View.VISIBLE
                    }
                    mainHandler.post(runnable)
                }
            }
        } else {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    cityName = list[0].adminArea
                    Timber.d("getCityName: $cityName")

                    val mainHandler = Handler(Looper.getMainLooper())
                    val runnable = Runnable {
                        mCreateTransactionFragmentBinding.tvTransactionLocation.text = cityName
                        mCreateTransactionFragmentBinding.tvTransactionLocation.visibility = View.VISIBLE
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
}
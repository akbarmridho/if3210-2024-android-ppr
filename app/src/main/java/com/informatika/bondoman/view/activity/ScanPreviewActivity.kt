package com.informatika.bondoman.view.activity

import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.ActivityScanPreviewBinding
import com.informatika.bondoman.model.local.entity.transaction.Location
import com.informatika.bondoman.util.LocationUtil
import com.informatika.bondoman.viewmodel.scanner.ScanPreviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.util.Locale


class ScanPreviewActivity : AppCompatActivity() {
    private lateinit var mScanPreviewBinding: ActivityScanPreviewBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var location: Location? = null
    private val scanPreviewViewModel: ScanPreviewViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScanPreviewBinding = ActivityScanPreviewBinding.inflate(layoutInflater)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        setContentView(mScanPreviewBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("image", Uri::class.java)
        } else {
            intent.getParcelableExtra("image") as Uri?
        }

        mScanPreviewBinding.imageView.setImageURI(image)

        mScanPreviewBinding.cancelScan.setOnClickListener {
            finish()
        }

        mScanPreviewBinding.continueScan.setOnClickListener {
            if (image != null) {
                val stream = contentResolver.openInputStream(image)

                if (stream != null) {
                    scanPreviewViewModel.scanImage(stream.readBytes(), location)
                    stream.close()
                }
            }
        }

        scanPreviewViewModel.scanResult.observe(this@ScanPreviewActivity) {
            if (it.success != null) {
                finish()
                showToast(it.success)
            }

            if (it.error != null) {
                showToast(it.error)
            }
        }
    }

    private fun showToast(@StringRes string: Int) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if (LocationUtil.checkLocationPermission(this@ScanPreviewActivity)) {
            if (LocationUtil.isLocationEnabled(this@ScanPreviewActivity)) {
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
                            LocationServices.getFusedLocationProviderClient(this@ScanPreviewActivity)
                        fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    val lastLocation: android.location.Location =
                                        locationResult.lastLocation
                                    getCityName(lastLocation.latitude, lastLocation.longitude)
                                }
                            },
                            Looper.myLooper() ?: Looper.getMainLooper()
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

    private fun getCityName(lat: Double, lon: Double) {
        Timber.d("getCityName: $lat, $lon")
        var adminArea: String?
        val geocoder = Geocoder(this@ScanPreviewActivity, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { list ->
                if (list.size != 0) {
                    adminArea = list[0].adminArea
                    location = Location(
                        lat,
                        lon,
                        adminArea!!
                    )
                }
            }
        } else {
            try {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    adminArea = list[0].adminArea
                    location = Location(
                        lat,
                        lon,
                        adminArea!!
                    )
                    Timber.d("getAdminArea: $adminArea")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
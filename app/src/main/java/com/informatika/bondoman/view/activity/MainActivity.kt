package com.informatika.bondoman.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.ActivityMainBinding
import com.informatika.bondoman.model.local.entity.transaction.Transaction
import com.informatika.bondoman.prefdatastore.isfirsttime.IsFirstTime
import com.informatika.bondoman.util.LocationUtil
import com.informatika.bondoman.view.adapter.TransactionRecyclerAdapter
import com.informatika.bondoman.view.fragment.ReportFragment
import com.informatika.bondoman.view.fragment.ScannerFragment
import com.informatika.bondoman.view.fragment.TwibbonFragment
import com.informatika.bondoman.view.fragment.transaction.DetailTransactionFragment
import com.informatika.bondoman.view.fragment.transaction.ListTransactionFragment
import com.informatika.bondoman.viewmodel.JWTViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity(), TransactionRecyclerAdapter.ItemTouchListener {

    private lateinit var binding: ActivityMainBinding
    private val jwtViewModel: JWTViewModel by viewModel()
    private val isFirstTime: IsFirstTime by inject()

    override fun onItemClick(transaction: Transaction) {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_activity_container, DetailTransactionFragment.newInstance(transaction))
            .addToBackStack(detailTransactionFragmentTag)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_transaction,
                R.id.navigation_scanner,
                R.id.navigation_twibbon,
                R.id.navigation_report,
                R.id.navigation_settings
            )
        )
        navView.setupWithNavController(navController)

        // on click listener for each navigation item
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_transaction -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_activity_container, ListTransactionFragment.newInstance())
                        .commit()
                }
                R.id.navigation_scanner -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_activity_container, ScannerFragment.newInstance())
                        .commit()
                }
                R.id.navigation_twibbon -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_activity_container, TwibbonFragment.newInstance())
                        .commit()
                }
                R.id.navigation_report -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_activity_container, ReportFragment.newInstance())
                        .commit()
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener false
                }
                else -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_activity_container, ListTransactionFragment.newInstance())
                        .commit()
                }
            }
            return@setOnItemSelectedListener true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, ListTransactionFragment.newInstance())
                .commit()
        }

    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            val isExpired = jwtViewModel.isExpired()
            if (isExpired) {
                jwtViewModel.jwtManager.onLogout()
                Toast.makeText(this@MainActivity, "Token expired", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            if (!LocationUtil.checkLocationPermission(this@MainActivity)) {
                isFirstTime.isFirstTime().collect {
                    if (it) {
                        LocationUtil.requestLocationPermission(this@MainActivity)
                        isFirstTime.setFirstTimeToFalse()
                    }
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationUtil.LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("Location permission granted")
            } else {
                Timber.e("Location permission denied")
            }
        }
    }

    companion object {
        const val detailTransactionFragmentTag = "detail_transaction_fragment"
        const val listTransactionFragmentTag = "list_transaction_fragment"
        const val createTransactionFragmentTag = "create_transaction_fragment"
        const val updateTransactionFragmentTag = "update_transaction_fragment"
        const val settingFragmentTag = "settings_fragment"

    }

}
package com.informatika.bondoman.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.BuildConfig
import com.informatika.bondoman.MainActivity
import com.informatika.bondoman.databinding.ActivityMainEmptyBinding
import com.informatika.bondoman.di.applicationModule
import com.informatika.bondoman.di.databaseModule
import com.informatika.bondoman.di.networkModule
import com.informatika.bondoman.di.repositoryModule
import com.informatika.bondoman.di.viewModelModule
import com.informatika.bondoman.prefdatastore.JWTManager
import com.informatika.bondoman.viewmodel.JWTViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin
import timber.log.Timber

class MainEmptyActivity : AppCompatActivity() {
    private val jwtViewModel: JWTViewModel by viewModel()
    private lateinit var binding: ActivityMainEmptyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initTimber()
        initKoin()

        binding = ActivityMainEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable support for Splash Screen API for
        // proper Android 12+ support
        installSplashScreen()

        enableEdgeToEdge()
    }

    override fun onStart() {
        super.onStart()
        if (BuildConfig.DEBUG) {
            Timber.d("onStart")
        }

        lifecycleScope.launch {
            if (jwtViewModel.isExpired()) {
                val intent = Intent(this@MainEmptyActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@MainEmptyActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MainEmptyActivity)
            modules(
                listOf(
                    applicationModule,
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
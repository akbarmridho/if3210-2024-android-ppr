package com.informatika.bondoman.view.activity

import android.content.Context
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
import com.informatika.bondoman.viewmodel.JWTViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainEmptyActivity : AppCompatActivity() {
    private val jwtViewModel: JWTViewModel by viewModel()
    private lateinit var binding: ActivityMainEmptyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTimber()
        initKoin(applicationContext)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable support for Splash Screen API for
        // proper Android 12+ support
        installSplashScreen()

        enableEdgeToEdge()

        lifecycleScope.launch {
            jwtViewModel.jwtManager.isAuthenticated().collect{
                if (it) {
                    val intent = Intent(this@MainEmptyActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    val intent = Intent(this@MainEmptyActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun initKoin(mContext: Context) {
        Timber.tag("Koin").d("Init Koin")
        GlobalContext.getOrNull() ?: startKoin {
            androidContext(mContext)
            modules(
                listOf(
                    applicationModule,
                    networkModule,
                    databaseModule,
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
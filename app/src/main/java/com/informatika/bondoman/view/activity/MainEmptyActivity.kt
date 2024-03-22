package com.informatika.bondoman.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.databinding.ActivityMainEmptyBinding
import com.informatika.bondoman.prefdatastore.JWTManager
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainEmptyActivity : AppCompatActivity() {
    private val jwtManager: JWTManager by inject()
    private lateinit var binding: ActivityMainEmptyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable support for Splash Screen API for
        // proper Android 12+ support
        installSplashScreen()

        enableEdgeToEdge()

        lifecycleScope.launch {
            jwtManager.isAuthenticated().collect{
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
}
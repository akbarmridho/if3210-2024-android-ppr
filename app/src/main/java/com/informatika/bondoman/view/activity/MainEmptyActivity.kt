package com.informatika.bondoman.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.MainActivity
import com.informatika.bondoman.databinding.ActivityMainEmptyBinding
import com.informatika.bondoman.view.login.LoginActivity
import com.informatika.bondoman.prefdatastore.JWTManager
import kotlinx.coroutines.launch

class MainEmptyActivity : AppCompatActivity() {
    private lateinit var jwtManager: JWTManager
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
        // Hide the header bar

        jwtManager = JWTManager(applicationContext)
        lifecycleScope.launch {
            jwtManager.isAuthenticated().collect {
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
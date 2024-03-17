package com.informatika.bondoman

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.databinding.ActivityMainEmptyBinding
import com.informatika.bondoman.ui.login.LoginActivity
import com.informatika.bondoman.utils.jwt.JWTManager
import kotlinx.coroutines.launch

class MainEmptyActivity : AppCompatActivity() {
    private lateinit var jwtManager: JWTManager
    private lateinit var binding: ActivityMainEmptyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        // Hide the header bar

        jwtManager = JWTManager(applicationContext)
        lifecycleScope.launch {
            jwtManager.isAuthenticated().collect() {
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
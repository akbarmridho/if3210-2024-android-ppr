package com.informatika.bondoman.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.databinding.ActivitySettingsBinding
import com.informatika.bondoman.viewmodel.JWTViewModel
import com.informatika.bondoman.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.random.Random

const val BROADCAST_TRANSACTION = "com.informatika.bondomanapp.receiver.RandomizeTransactionReceiver"

class SettingsActivity: AppCompatActivity() {
    lateinit var mSettingsActivityBinding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val jwtViewModel: JWTViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSettingsActivityBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mSettingsActivityBinding.root)

        val btnBroadcastTransaction = mSettingsActivityBinding.btnBroadcastTransaction
        val btnLogout = mSettingsActivityBinding.btnLogout

        btnBroadcastTransaction.setOnClickListener {
            val intent = Intent(BROADCAST_TRANSACTION)
                .putExtra("amount", Random.nextInt(0, Int.MAX_VALUE))
            sendBroadcast(intent)
        }

        btnLogout.setOnClickListener {
            Timber.d("Logout clicked");
            logout();
        }
    }

    fun logout() {
        lifecycleScope.launch {
            jwtViewModel.jwtManager.onLogout();
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java);
            Toast.makeText(this@SettingsActivity, "Logout Succesful", Toast.LENGTH_SHORT).show()
            startActivity(intent);
            finishAffinity()
        }
    }
}
package com.informatika.bondoman.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.informatika.bondoman.databinding.ActivitySettingsBinding
import com.informatika.bondoman.viewmodel.SettingsViewModel
import kotlin.random.Random

const val BROADCAST_TRANSACTION = "com.informatika.bondomanapp.receiver.RandomizeTransactionReceiver"

class SettingsActivity: AppCompatActivity() {
    lateinit var mSettingsActivityBinding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSettingsActivityBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mSettingsActivityBinding.root)

        val btnBroadcastTransaction = mSettingsActivityBinding.btnBroadcastTransaction
        btnBroadcastTransaction.setOnClickListener {
            val intent = Intent(BROADCAST_TRANSACTION)
                .putExtra("amount", Random.nextInt(0, Int.MAX_VALUE))
            sendBroadcast(intent)
        }
    }
}
package com.informatika.bondoman.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.databinding.ActivitySettingsBinding
import com.informatika.bondoman.viewmodel.JWTViewModel
import com.informatika.bondoman.viewmodel.SettingsViewModel
import com.informatika.bondoman.viewmodel.login.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

const val BROADCAST_TRANSACTION = "com.informatika.bondomanapp.receiver.RandomizeTransactionReceiver"

class SettingsActivity: AppCompatActivity() {
    lateinit var mSettingsActivityBinding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val jwtViewModel: JWTViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSettingsActivityBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mSettingsActivityBinding.root)

        val btnBroadcastTransaction = mSettingsActivityBinding.btnBroadcastTransaction
        val btnLogout = mSettingsActivityBinding.btnLogout
        val btnSend = mSettingsActivityBinding.btnSend

        btnBroadcastTransaction.setOnClickListener {
            val intent = Intent(BROADCAST_TRANSACTION)
                .putExtra("amount", Random.nextInt(0, Int.MAX_VALUE))
            sendBroadcast(intent)
        }

        btnLogout.setOnClickListener {
            logout();
        }

        btnSend.setOnClickListener {
            sendEmail();
        }

    }

    fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(loginViewModel.getEmail()))
            putExtra(Intent.EXTRA_SUBJECT, "Spreadsheet of Transaction")
            putExtra(Intent.EXTRA_TEXT, "Attached to this email is the spreadsheet of all of your transaction so far.")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Transaction Through Email"))
        }
    }

    fun logout() {
        lifecycleScope.launch {
            jwtViewModel.jwtManager.onLogout();
            loginViewModel.logout();
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java);
            Toast.makeText(this@SettingsActivity, "Logout Succesful", Toast.LENGTH_SHORT).show()
            startActivity(intent);
            finishAffinity()
        }
    }
}
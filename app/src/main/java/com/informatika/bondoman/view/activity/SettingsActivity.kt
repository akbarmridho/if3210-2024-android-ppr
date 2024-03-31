package com.informatika.bondoman.view.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.informatika.bondoman.databinding.ActivitySettingsBinding
import com.informatika.bondoman.model.local.ExportType
import com.informatika.bondoman.viewmodel.JWTViewModel
import com.informatika.bondoman.viewmodel.SettingsViewModel
import com.informatika.bondoman.viewmodel.login.LoginViewModel
import com.informatika.bondoman.viewmodel.transaction.ExporterTransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.random.Random


const val BROADCAST_TRANSACTION =
    "com.informatika.bondomanapp.receiver.RandomizeTransactionReceiver"

class SettingsActivity : NetworkAwareActivity() {
    private lateinit var mSettingsActivityBinding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val jwtViewModel: JWTViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private val exporterTransactionViewModel: ExporterTransactionViewModel by viewModel()
    private lateinit var exportXlsxLauncher: ActivityResultLauncher<Intent>
    private lateinit var exportXlsLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSettingsActivityBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mSettingsActivityBinding.root)

        val btnBroadcastTransaction = mSettingsActivityBinding.btnBroadcastTransaction
        val btnLogout = mSettingsActivityBinding.btnLogout
        val btnSend = mSettingsActivityBinding.btnSend
        val btnBack = mSettingsActivityBinding.btnBack
        val btnExportXlsx = mSettingsActivityBinding.btnExportXlsx
        val btnExportXls = mSettingsActivityBinding.btnExportXls

        btnBroadcastTransaction.setOnClickListener {
            val intent = Intent(BROADCAST_TRANSACTION)
                .putExtra("amount", Random.nextInt(0, Int.MAX_VALUE))
            sendBroadcast(intent)
        }

        btnLogout.setOnClickListener {
            logout()
        }

        btnSend.setOnClickListener {
            sendEmail()
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnExportXlsx.setOnClickListener {
            exportXlsx()
        }

        btnExportXls.setOnClickListener {
            exportXls()
        }

        exportXlsxLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        val uri = result.data!!.data
                        if (uri != null) {
                            lifecycleScope.launch {
                                exportFile(uri, ExportType.XLSX)
                            }
                        }
                    }
                }
            }

        exportXlsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data != null) {
                        val uri = result.data!!.data
                        if (uri != null) {
                            lifecycleScope.launch {
                                exportFile(uri, ExportType.XLS)
                            }
                        }
                    }
                }
            }
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(loginViewModel.getEmail()))
            putExtra(Intent.EXTRA_SUBJECT, "Spreadsheet of Transaction")
            putExtra(
                Intent.EXTRA_TEXT,
                "Attached to this email is the spreadsheet of all of your transaction so far."
            )
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send Transaction Through Email"))
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            jwtViewModel.jwtManager.onLogout()
            loginViewModel.logout()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            Toast.makeText(this@SettingsActivity, "Logout Succesful", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finishAffinity()
        }
    }

    private suspend fun exportFile(uri: Uri, exportType: ExportType) {
        withContext(Dispatchers.IO) {
            val mainHandler = Handler(Looper.getMainLooper())
            val descriptor = contentResolver.openFileDescriptor(uri, "w")

            val result = exporterTransactionViewModel.export(exportType)

            if (result == null) {
                mainHandler.post {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Failed to export data",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                return@withContext
            }

            if (descriptor != null) {
                val fileOutputStream = FileOutputStream(descriptor.fileDescriptor)

                fileOutputStream.write(result.toByteArray())
                fileOutputStream.close()
                descriptor.close()

                mainHandler.post {
                    Toast.makeText(
                        this@SettingsActivity,
                        "File saved",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                mainHandler.post {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Failed to get descriptor",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun exportXlsx() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        val time = LocalDateTime.now().toString()
        val filename = "transaction_$time.xlsx"

        intent.putExtra(Intent.EXTRA_TITLE, filename)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Download"))
        exportXlsxLauncher.launch(intent)
    }

    private fun exportXls() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.ms-excel"

        val time = LocalDateTime.now().toString()
        val filename = "transaction_$time.xls"

        intent.putExtra(Intent.EXTRA_TITLE, filename)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("/Download"))
        exportXlsxLauncher.launch(intent)
    }
}
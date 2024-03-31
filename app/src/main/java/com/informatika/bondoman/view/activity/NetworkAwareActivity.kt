package com.informatika.bondoman.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.informatika.bondoman.viewmodel.connectivity.ConnectivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NetworkAwareActivity : AppCompatActivity() {
    protected val connectivityViewModel: ConnectivityViewModel by viewModel()
    protected var isOnline = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityViewModel.isOnline.observe(this) {
            this.isOnline = it
            if(!this.isOnline) {
                Toast.makeText(
                    applicationContext,
                    "You're not connected to the internet!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
}
package com.informatika.bondoman.model.repository.connectivity

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConnectivityRepositoryImpl(context: Context) : ConnectivityRepository {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)

    override val isConnected: Flow<Boolean> = _isConnected

    override fun lastStatus(): Boolean {
        return _isConnected.value
    }

    override val isToastSent: MutableMap<Boolean, Boolean> =
        mutableMapOf(true to false, false to false)

    override fun markToastSent(connectivityStatus: Boolean) {
        isToastSent[connectivityStatus] = true
    }

    fun markToastNotSent(connectivityStatus: Boolean) {
        isToastSent[connectivityStatus] = false
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                _isConnected.value = true
                markToastNotSent(true)
            }

            override fun onLost(network: android.net.Network) {
                _isConnected.value = false
                markToastNotSent(false)
            }
        })
    }
}
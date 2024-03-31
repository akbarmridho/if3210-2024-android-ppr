package com.informatika.bondoman.model.repository.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    val isConnected: Flow<Boolean>
    val isToastSent: MutableMap<Boolean, Boolean>

    abstract fun markToastSent(connectivityStatus : Boolean)
}
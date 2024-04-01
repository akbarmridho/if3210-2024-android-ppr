package com.informatika.bondoman.model.repository.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    val isConnected: Flow<Boolean>
    val isToastSent: MutableMap<Boolean, Boolean>

    fun markToastSent(connectivityStatus: Boolean)

    fun lastStatus(): Boolean
}
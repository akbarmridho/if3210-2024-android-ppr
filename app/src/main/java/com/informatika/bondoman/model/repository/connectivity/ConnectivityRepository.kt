package com.informatika.bondoman.model.repository.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    val isConnected: Flow<Boolean>
}
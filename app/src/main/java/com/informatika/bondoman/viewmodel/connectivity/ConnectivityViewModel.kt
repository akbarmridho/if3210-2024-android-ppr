package com.informatika.bondoman.viewmodel.connectivity

import com.informatika.bondoman.model.repository.connectivity.ConnectivityRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow

class ConnectivityViewModel(private val connectivityRepository: ConnectivityRepository) : ViewModel() {
    val isOnline = connectivityRepository.isConnected.asLiveData()


    fun getConnectivityFlow(): Flow<Boolean> {
        return this.connectivityRepository.isConnected
    }

    fun getIsToastSent(): MutableMap<Boolean, Boolean> {
        return this.connectivityRepository.isToastSent
    }

    fun markToastSent(connectivityStatus : Boolean) {
        this.connectivityRepository.markToastSent(connectivityStatus)
    }
}
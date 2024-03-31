package com.informatika.bondoman.viewmodel.connectivity

import com.informatika.bondoman.model.repository.connectivity.ConnectivityRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

class ConnectivityViewModel(val connectivityRepository: ConnectivityRepository) : ViewModel() {
    val isOnline = connectivityRepository.isConnected.asLiveData()
}
package com.informatika.bondoman.viewmodel.connectivity

import com.informatika.bondoman.model.repository.connectivity.ConnectivityRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class ConnectivityViewModel(private val connectivityRepository: ConnectivityRepository) : ViewModel() {

    val isOnline = connectivityRepository.isConnected.asLiveData()
}
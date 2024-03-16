package com.informatika.bondoman.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.utils.JWTManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JWTViewModel(private val jwtManager: JWTManager): ViewModel() {
    val token = MutableLiveData<String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            jwtManager.getToken().collect {
                withContext(Dispatchers.Main) {
                    token.value = it
                }
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            jwtManager.saveToken(token)
        }
    }

    fun deleteToken() {
        viewModelScope.launch(Dispatchers.IO) {
            jwtManager.onLogout()
        }
    }
}
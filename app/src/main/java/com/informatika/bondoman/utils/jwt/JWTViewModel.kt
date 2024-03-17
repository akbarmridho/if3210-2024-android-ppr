package com.informatika.bondoman.utils.jwt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JWTViewModel(private val jwtManager: JWTManager): ViewModel() {
    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    fun isAuthenticated() : Boolean {
        var isAuth = false
        viewModelScope.launch(Dispatchers.IO) {
            jwtManager.isAuthenticated().collect {
                withContext(Dispatchers.Main) {
                    isAuth = it
                }
            }
        }
        return isAuth
    }

    fun getToken() {
        viewModelScope.launch(Dispatchers.IO) {
            var isAuth = false
            jwtManager.isAuthenticated().collect {
                withContext(Dispatchers.Main) {
                    isAuth = it
                }
            }

            if (!isAuth) {
                return@launch
            }

            jwtManager.getToken().let {
                _token.postValue(it)
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
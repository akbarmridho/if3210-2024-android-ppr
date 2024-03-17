package com.informatika.bondoman.utils.jwt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.utils.jwt.JWTManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JWTViewModel(private val jwtManager: JWTManager): ViewModel() {
    val token = MutableLiveData<String>()

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
                token.postValue(it)
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
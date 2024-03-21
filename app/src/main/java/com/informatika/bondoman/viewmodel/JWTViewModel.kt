package com.informatika.bondoman.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.repository.token.TokenRepository
import com.informatika.bondoman.prefdatastore.JWTManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class JWTViewModel(var jwtManager: JWTManager, private var tokenRepository: TokenRepository): ViewModel() {
    suspend fun isExpired(): Boolean {
        var isExpired = true
        try {
            val result = tokenRepository.token(jwtManager.getToken())
            if (result is Resource.Success) {
                isExpired = false
            } else {
                jwtManager.onLogout()
            }
        } catch (e: Exception) {
            Timber.tag("JWT").d("No token found")
        }
        return isExpired
    }

}
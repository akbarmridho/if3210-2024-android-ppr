package com.informatika.bondoman.viewmodel

import androidx.lifecycle.ViewModel
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.repository.token.TokenRepository
import com.informatika.bondoman.prefdatastore.jwt.JWTManager
import timber.log.Timber

class JWTViewModel(var jwtManager: JWTManager, private var tokenRepository: TokenRepository) :
    ViewModel() {
    suspend fun isExpired(): Boolean {
        try {
            return when (val result = tokenRepository.token(jwtManager.getToken())) {
                is Resource.Success -> {
                    !result.data
                }

                else -> {
                    jwtManager.onLogout()
                    true
                }
            }
        } catch (e: Exception) {
            Timber.tag("JWT").d("No token found")
            return true // true here because user need to reach past login in order for this function to run
        }
    }
}
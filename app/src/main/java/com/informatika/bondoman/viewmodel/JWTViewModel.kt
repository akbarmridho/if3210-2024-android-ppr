package com.informatika.bondoman.viewmodel

import androidx.lifecycle.ViewModel
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.model.repository.token.TokenRepository
import com.informatika.bondoman.prefdatastore.jwt.JWTManager
import timber.log.Timber
import java.net.UnknownHostException

class JWTViewModel(
    var jwtManager: JWTManager,
    private var tokenRepository: TokenRepository,
) :
    ViewModel() {

    suspend fun isExpired(): Boolean {
        try {
            return when (val result = tokenRepository.token(jwtManager.getToken())) {
                is Resource.Success -> {
                    return !result.data
                }

                is Resource.Error -> {
                    if (result.throwable is UnknownHostException) {
                        // cannot request due to connectivity error
                        return false
                    }
                    jwtManager.onLogout()
                    return true
                }

                else -> {
                    return false
                }
            }
        } catch (e: Exception) {
            Timber.tag("JWT").d("No token found")
            return true // true here because user need to reach past login in order for this function to run
        }
    }
}
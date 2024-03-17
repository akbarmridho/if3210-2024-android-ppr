package com.informatika.bondoman.utils.jwt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.informatika.bondoman.data.repository.TokenRepository
import com.informatika.bondoman.utils.ApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jwt_token")

class JWTManager(private val context: Context) {
    // returns a flow of is authenticated state

    suspend fun isAuthenticated(): Flow<Boolean> {
        // flow of token existence from dataStore
        val isStoreToken =  context.dataStore.data.map {
            it.contains(KEY_TOKEN)
        }
        if (isStoreToken.equals(true)){
            // check token
            val token = context.dataStore.data.map {
                it[KEY_TOKEN]
            }

            val tokenRepository = TokenRepository()
            val result = tokenRepository.token(token.toString())
            if (result is ApiResponse.Success){
                return isStoreToken
            } else {
                onLogout()
                return context.dataStore.data.map {
                    it.contains(KEY_TOKEN)
                }
            }
        } else {
            return isStoreToken
        }
    }

    suspend fun getToken(): String {
        return context.dataStore.data
            .map { it[KEY_TOKEN] } // get a flow of token from dataStore
            .firstOrNull() // transform flow to suspend
            ?: throw IllegalArgumentException("no token stored")
    }

    // store new token after sign in or token refresh
    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
        }
    }

    // to call when user logs out or when refreshing the token has failed
    suspend fun onLogout() {
        context.dataStore.edit {
            it.remove(KEY_TOKEN)
        }
    }

    companion object {
        val KEY_TOKEN = stringPreferencesKey("jwt_token")
    }
}
package com.informatika.bondoman.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JWTManager(private val dataStore: DataStore<Preferences>) {
    // returns a flow of is authenticated state
    fun isAuthenticated(): Flow<Boolean> {
        // flow of token existence from dataStore
        return dataStore.data.map {
            it.contains(KEY_TOKEN)
        }
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map {
            it[KEY_TOKEN]
        }
    }

    // store new token after sign in or token refresh
    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[KEY_TOKEN] = token
        }
    }

    // to call when user logs out or when refreshing the token has failed
    suspend fun onLogout() {
        dataStore.edit {
            it.remove(KEY_TOKEN)
        }
    }

    companion object {
        @Volatile
        private var instance: JWTManager? = null

        fun getInstance(dataStore: DataStore<Preferences>): JWTManager {
            return instance ?: synchronized(this) {
                instance ?: JWTManager(dataStore).also { instance = it }
            }
        }

        val KEY_TOKEN = stringPreferencesKey("jwt_token")
    }
}
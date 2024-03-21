package com.informatika.bondoman.prefdatastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

const val KEY_TOKEN = "jwt_token"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = KEY_TOKEN)

class JWTManagerImpl(private val context: Context) : JWTManager {

    // returns a flow of is authenticated state
    override fun isAuthenticated(): Flow<Boolean> {
        // flow of token existence from dataStore
        return context.dataStore.data.map {
            it.contains(JWT_TOKEN)
        }
    }

    override suspend fun getToken(): String {
        return context.dataStore.data
            .map { it[JWT_TOKEN] } // get a flow of token from dataStore
            .firstOrNull() // transform flow to suspend
            ?: throw IllegalArgumentException("no token stored")
    }

    // store new token after sign in or token refresh
    override suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[JWT_TOKEN] = token
        }
    }

    // to call when user logs out or when refreshing the token has failed
    override suspend fun onLogout() {
        context.dataStore.edit {
            it.remove(JWT_TOKEN)
        }
    }

    companion object {
        val JWT_TOKEN = stringPreferencesKey(KEY_TOKEN)
    }
}
package com.informatika.bondoman.prefdatastore.isfirsttime

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val IS_FIRST_TIME = "is_first_time"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = IS_FIRST_TIME)

class IsFirstTimeImpl(private val context: Context) : IsFirstTime {

    // get the value of isFirstUser from dataStore
    override fun isFirstTime(): Flow<Boolean> {
        return context.dataStore.data.map {
            !it.contains(_IS_FIRST_TIME)
        }
    }

    // set isFirstUser to false after the first user has been set
    override suspend fun setFirstTimeToFalse() {
        context.dataStore.edit {
            it[_IS_FIRST_TIME] = false.toString()
        }
    }

    companion object {
        val _IS_FIRST_TIME = stringPreferencesKey(IS_FIRST_TIME)
    }
}
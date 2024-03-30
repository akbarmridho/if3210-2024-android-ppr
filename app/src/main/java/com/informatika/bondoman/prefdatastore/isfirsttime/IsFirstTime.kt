package com.informatika.bondoman.prefdatastore.isfirsttime

import kotlinx.coroutines.flow.Flow

interface IsFirstTime {
    fun isFirstTime(): Flow<Boolean>
    suspend fun setFirstTimeToFalse()
}
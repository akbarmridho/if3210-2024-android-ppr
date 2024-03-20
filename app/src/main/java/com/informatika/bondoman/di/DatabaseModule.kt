package com.informatika.bondoman.di

import androidx.room.Room
import com.informatika.bondoman.model.local.AppDB
import com.informatika.bondoman.model.local.DBConstants
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // Dependency: AppDB
    single {
        Room.databaseBuilder(androidContext(), AppDB::class.java, DBConstants.mName).build()
    }

    single {
        val appDB: AppDB = get()
        appDB.transactionDao()
    }
}
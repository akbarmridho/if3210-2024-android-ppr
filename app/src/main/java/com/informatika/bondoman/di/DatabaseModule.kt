package com.informatika.bondoman.di

import androidx.room.Room
import com.informatika.bondoman.model.local.AppDatabase
import com.informatika.bondoman.model.local.DBConstants
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // Dependency: AppDB
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, DBConstants.mName)
            .fallbackToDestructiveMigration()
            .build()
    }

    // Dependency: TransactionDao
    single {
        get<AppDatabase>().transactionDao()
    }
}
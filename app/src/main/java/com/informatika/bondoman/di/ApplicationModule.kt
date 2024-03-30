package com.informatika.bondoman.di

import com.informatika.bondoman.prefdatastore.isfirsttime.IsFirstTime
import com.informatika.bondoman.prefdatastore.isfirsttime.IsFirstTimeImpl
import com.informatika.bondoman.prefdatastore.jwt.JWTManager
import com.informatika.bondoman.prefdatastore.jwt.JWTManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module

val applicationModule = module {
    // Dependency: JWTManager
    single<JWTManager> {
        JWTManagerImpl(get())
    }

    // Dependency: IsFirstTime
    single<IsFirstTime> {
        IsFirstTimeImpl(get())
    }

    // Dependency: Moshi
    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
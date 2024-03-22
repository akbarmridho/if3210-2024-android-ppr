package com.informatika.bondoman.di

import com.informatika.bondoman.prefdatastore.JWTManager
import com.informatika.bondoman.prefdatastore.JWTManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module

val applicationModule = module {
    // Dependency: JWTManager
    single<JWTManager> {
        JWTManagerImpl(get())
    }

    // Dependency: Moshi
    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
}
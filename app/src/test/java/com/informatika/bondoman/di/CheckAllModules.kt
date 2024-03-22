package com.informatika.bondoman.di

import android.app.Application
import com.informatika.bondoman.di.applicationModule
import com.informatika.bondoman.di.databaseModule
import com.informatika.bondoman.di.networkModule
import com.informatika.bondoman.di.repositoryModule
import com.informatika.bondoman.di.viewModelModule
import io.mockk.mockk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.Test

class CheckAllModules: KoinTest {
    private val mockApplication: Application = mockk()
    @Test
    fun checkAllModules() {
        // Check all modules
        startKoin {
            androidContext(mockApplication)
            modules(listOf(applicationModule, databaseModule, networkModule, repositoryModule, viewModelModule))
        }.checkModules()

    }
}
package com.informatika.bondoman

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import com.informatika.bondoman.backgroundservice.AuthService
import com.informatika.bondoman.di.applicationModule
import com.informatika.bondoman.di.databaseModule
import com.informatika.bondoman.di.networkModule
import com.informatika.bondoman.di.repositoryModule
import com.informatika.bondoman.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class BondomanApp : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initTimber()
        initKoin()

        val serviceIntent = Intent(this, AuthService::class.java)
        startService(serviceIntent)
    }

    private fun initKoin() {
        Timber.tag("Koin").d("Init Koin")
        GlobalContext.getOrNull() ?: startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BondomanApp)
            modules(
                listOf(
                    applicationModule,
                    networkModule,
                    databaseModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
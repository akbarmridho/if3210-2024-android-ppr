package com.informatika.bondoman

import android.app.Application
import com.informatika.bondoman.di.applicationModule
import com.informatika.bondoman.di.databaseModule
import com.informatika.bondoman.di.networkModule
import com.informatika.bondoman.di.repositoryModule
import com.informatika.bondoman.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import timber.log.Timber

class BondomanApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initTimber()
        initKoin()
    }

    private fun initKoin() {
        Timber.tag("Koin").d("Init Koin")
        GlobalContext.getOrNull() ?: startKoin {
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
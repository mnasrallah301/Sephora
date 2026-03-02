package com.aventique.sephora

import android.app.Application
import com.aventique.sephora.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SephoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@SephoraApplication)
            modules(appModule())
        }
    }
}

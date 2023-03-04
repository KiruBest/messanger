package com.example.messanger.app.application

import android.app.Application
import com.example.messanger.app.di.dataModule
import com.example.messanger.app.di.firebaseModule
import com.example.messanger.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinInitializer: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinInitializer)
            modules(
                viewModelModule,
                dataModule,
                firebaseModule
            )
        }
    }
}
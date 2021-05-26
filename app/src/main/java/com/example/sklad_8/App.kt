package com.example.sklad_8

import android.app.Application
import com.example.sklad_8.di.applicationModule
import com.example.sklad_8.di.networkModule
import com.example.sklad_8.di.viewModelModule
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.android.AppReducer
import com.github.terrakok.modo.android.LogReducer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        modo = Modo(LogReducer(AppReducer(this, MultiReducer())))
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    applicationModule,
                    networkModule,
                    viewModelModule
                )
            )
        }
    }

    companion object {
        lateinit var modo: Modo
            private set
    }


}
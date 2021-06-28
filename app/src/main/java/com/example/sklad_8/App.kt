package com.example.sklad_8

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.sklad_8.di.applicationModule
import com.example.sklad_8.di.networkModule
import com.example.sklad_8.di.viewModelModule
import com.example.sklad_8.di.workModule
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.android.AppReducer
import com.github.terrakok.modo.android.LogReducer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import timber.log.Timber.DebugTree

import timber.log.Timber
import java.lang.Exception


class App : Application(), KoinComponent {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate() {
        modo = Modo(LogReducer(AppReducer(this, MultiReducer())))
        super.onCreate()

        INSTANCE = this

        Timber.plant(if (BuildConfig.DEBUG) DebugTree() else CrashReportingTree())

        Timber.plant(DebugTree())

        startKoin {

            androidLogger()
            androidContext(this@App)
            workManagerFactory()

            modules(
                listOf(
                    applicationModule,
                    networkModule,
                    viewModelModule,
                    workModule
                )
            )
        }
    }

    companion object {
        lateinit var INSTANCE: App
            private set
        lateinit var modo: Modo
            private set
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
        }
    }

}
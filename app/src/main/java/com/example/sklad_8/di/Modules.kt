package com.example.sklad_8.di

import android.content.Context
import android.util.Log
import com.example.sklad_8.BuildConfig
import com.example.sklad_8.data.db.SkladDatabase
import com.example.sklad_8.data.network.ApiService
import com.example.sklad_8.data.network.AuthenticationInterceptor
import com.example.sklad_8.data.network.HostSelectionInterceptor
import com.example.sklad_8.data.network.NetworkConnectionInterceptor
import com.example.sklad_8.data.prefs.SharedPrefsManager
import com.example.sklad_8.data.repositores.CommonSettingsRepository
import com.example.sklad_8.data.repositores.GoodsRepository
import com.example.sklad_8.data.repositores.SettingsRepository
import com.example.sklad_8.data.repositores.SyncRepository
import com.example.sklad_8.ui.goods.DetailGoodViewModel
import com.example.sklad_8.ui.goods.GoodsViewModel
import com.example.sklad_8.ui.settings.ServerSettingsViewModel
import com.example.sklad_8.ui.settings.common.CommonSettingsViewModel
import com.example.sklad_8.ui.sync.SyncViewModel
import com.google.gson.Gson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val OK_HTTP_CLIENT_TAG = "OK_HTTP_TAG"
private const val HOT_SELECTION_INTERCEPTOR_TAG = "HOT_SELECTION_INTERCEPTOR_TAG"
private const val LOGIN_INTERCEPTOR_TAG = "LOGIN_INTERCEPTOR_TAG"
private const val NETWORK_CONNECTION_INTERCEPTOR_TAG = "NETWORK_CONNECTION_INTERCEPTOR_TAG"
private const val RETROFIT_TAG = "RETROFIT_TAG"

val applicationModule = module {

    single {
        SharedPrefsManager(
            androidContext().getSharedPreferences(
                androidContext().packageName,
                Context.MODE_PRIVATE
            )
        )
    }

    single {
        SkladDatabase.getInstance(androidContext())
    }

    single {
        SyncRepository(
            api = get(named(RETROFIT_TAG)),
            db = get(),
            context = androidContext()
        )
    }
    single { GoodsRepository(db = get(), context = androidContext()) }
    single { SettingsRepository(prefs = get()) }
    single { CommonSettingsRepository(db = get()) }
}

val networkModule = module {

    single(named(NETWORK_CONNECTION_INTERCEPTOR_TAG)) { NetworkConnectionInterceptor(get()) } bind NetworkConnectionInterceptor::class
    single(named(HOT_SELECTION_INTERCEPTOR_TAG)) { HostSelectionInterceptor(get()) } bind HostSelectionInterceptor::class
    single(named(LOGIN_INTERCEPTOR_TAG)) {
        HttpLoggingInterceptor().also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    single(named(OK_HTTP_CLIENT_TAG)) {

        val okHttpBuilder = OkHttpClient.Builder()
            .addInterceptor(get(named(HOT_SELECTION_INTERCEPTOR_TAG)) as HostSelectionInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                    Timber.i(message)
                })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(loggingInterceptor)
        }
        okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.addNetworkInterceptor(get(named(LOGIN_INTERCEPTOR_TAG)) as HttpLoggingInterceptor)
        okHttpBuilder.build()

    } bind OkHttpClient::class

    single(named(RETROFIT_TAG)) {
        val okkHttpclient = OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor(get())).apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor =
                        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                            Timber.i(message)
                        })
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(loggingInterceptor)
                }
            }
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(get(named(LOGIN_INTERCEPTOR_TAG)) as HttpLoggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("http://91.225.192.41:55555")
            .client(okkHttpclient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}

val viewModelModule = module {
    viewModel { SyncViewModel(repository = get()) }
    viewModel { GoodsViewModel(goodsRepository = get()) }
    viewModel { ServerSettingsViewModel(repository = get()) }
    viewModel { DetailGoodViewModel(repository = get()) }
    viewModel { CommonSettingsViewModel(repository = get()) }
}
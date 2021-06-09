package com.example.sklad_8.data.network

import android.content.Context
import com.example.sklad_8.BuildConfig
import com.example.sklad_8.data.prefs.SharedPrefsManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object RemoteService {

    @Volatile
    private var INSTANCE: ApiService? = null

    fun getInstance(context: Context): ApiService {

        INSTANCE?.let { return INSTANCE as ApiService }

        val okkHttpclient = OkHttpClient.Builder()
            .addInterceptor(getAuthenticationInterceptor(context)).apply {
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
            .addNetworkInterceptor(HttpLoggingInterceptor().also {
                it.setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()

        return Retrofit.Builder()
            .baseUrl("http://91.225.192.41:55555")
            .client(okkHttpclient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun getAuthenticationInterceptor(context: Context): AuthenticationInterceptor {
       return AuthenticationInterceptor(
            SharedPrefsManager(
                context.getSharedPreferences(
                    context.packageName,
                    Context.MODE_PRIVATE
                )
            )
        )
    }

}
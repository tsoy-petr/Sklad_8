package com.example.sklad_8.data.network

import com.example.sklad_8.data.prefs.SharedPrefsManager
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor constructor(private val pref: SharedPrefsManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val host = pref.getServerAddress().toHttpUrlOrNull()
        val newRequest = host?.let {
            val newUrl = chain.request().url.newBuilder()
                .scheme(it.scheme)
                .host(it.toUrl().toURI().host)
                .port(it.port)
                .build()

            return@let chain.request().newBuilder()
                .url(newUrl)
                .build()
        }
        return if (newRequest != null) {
            chain.proceed(newRequest)
        } else {
            chain.proceed(
                chain.request().newBuilder().build()
            )
        }
    }
}
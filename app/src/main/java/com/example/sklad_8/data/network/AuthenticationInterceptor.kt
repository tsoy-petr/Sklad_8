package com.example.sklad_8.data.network

import com.example.sklad_8.data.prefs.SharedPrefsManager
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor(
    private val prefs: SharedPrefsManager
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val authToken = Credentials.basic(prefs.getLogin(), prefs.getPass());

        val original: Request = chain.request()
        val builder: Request.Builder = original.newBuilder()
            .header("Authorization", authToken)
        val request: Request = builder.build()
        return chain.proceed(request)
    }
}
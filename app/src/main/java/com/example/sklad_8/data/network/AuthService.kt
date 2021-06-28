package com.example.sklad_8.data.network

import com.example.sklad_8.data.prefs.SharedPrefsManager
import okhttp3.Credentials

class AuthService(private val prefs: SharedPrefsManager) {

    fun getToken() =
        Credentials.basic(prefs.getLogin(), prefs.getPass())
}
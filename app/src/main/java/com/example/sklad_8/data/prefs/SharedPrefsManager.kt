package com.example.sklad_8.data.prefs

import android.content.SharedPreferences

class SharedPrefsManager constructor(private val prefs: SharedPreferences) {

    companion object {
        private const val SERVER_ADDRESS = "server_address"
        private const val BLUETOOTH_NAME = "bluetooth_name"
        private const val BLUETOOTH_MAC = "bluetooth_mac"
        private const val TOKEN = "token"
        private const val LOGIN = "login"
        private const val PASSWORD = "password"
    }

    fun getLogin() = prefs.getString(LOGIN, "") ?: ""
    fun getPass() = prefs.getString(PASSWORD, "") ?: ""
    fun saveLogin(login: String) = prefs.edit().apply {
        putString(LOGIN, login)
    }.apply()
    fun savePass(pass: String) = prefs.edit().apply {
        putString(PASSWORD, pass)
    }.apply()

    fun getTaken() = prefs.getString(TOKEN, "") ?: ""

    fun saveServerAddress(serverAddress: String) {
        prefs.edit().apply {
            putString(SERVER_ADDRESS, serverAddress)
        }.apply()
    }

    fun getServerAddress() = prefs.getString(SERVER_ADDRESS, "") ?: ""

    fun saveBluetoothScanner(bluetoothScanner: BluetoothScanner) {
        prefs.edit().apply {
            putString(BLUETOOTH_NAME, bluetoothScanner.name)
            putString(BLUETOOTH_MAC, bluetoothScanner.mac)
        }.apply()
    }

    fun getBluetoothScanner(): BluetoothScanner? {
        val name = prefs.getString(BLUETOOTH_NAME, "")
        val mac = prefs.getString(BLUETOOTH_MAC, "")

        return if (!name.isNullOrEmpty()
            && !mac.isNullOrEmpty()
        ) {
            BluetoothScanner(name, mac)
        } else null
    }
}
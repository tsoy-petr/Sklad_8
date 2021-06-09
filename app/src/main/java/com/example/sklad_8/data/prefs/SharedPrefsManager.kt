package com.example.sklad_8.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.sklad_8.App
import com.example.sklad_8.data.services.BlDevice

class SharedPrefsManager constructor(private val prefs: SharedPreferences) {

    companion object {

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_MAC = "KEY_MAC"

        private const val SERVER_ADDRESS = "server_address"
        private const val BLUETOOTH_NAME = "bluetooth_name"
        private const val BLUETOOTH_MAC = "bluetooth_mac"
        private const val TOKEN = "token"
        private const val LOGIN = "login"
        private const val PASSWORD = "password"

        @Volatile
        private var INSTANCE: SharedPrefsManager? = null

        fun getInstance(): SharedPrefsManager {
            synchronized(this) {
                return INSTANCE ?: SharedPrefsManager(
                    App.INSTANCE.getSharedPreferences(
                        App.INSTANCE.packageName,
                        Context.MODE_PRIVATE
                    )
                ).also {
                    INSTANCE = it
                }
            }
        }
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

    fun saveDevice(bluetoothDevice: BlDevice) {
        prefs.edit().apply {
            putString(KEY_TITLE, bluetoothDevice.title)
            putString(KEY_MAC, bluetoothDevice.mac)
        }.apply()
    }

    fun getDevice() : BlDevice? {
        val title = prefs.getString(KEY_TITLE, "") ?: ""
        val mac = prefs.getString(KEY_MAC, "") ?: ""
        return if (title.isNotEmpty()
            && mac.isNotEmpty()) {
            BlDevice(title, mac)
        } else null
    }

    fun getMac() = prefs.getString(KEY_MAC, "") ?: ""
}
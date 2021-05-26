package com.example.sklad_8.data.prefs

import android.content.SharedPreferences

class SharedPrefsManager constructor(private val prefs: SharedPreferences){

    companion object {
        const val SERVER_ADDRESS = "server_address"
        const val BLUETOOTH_NAME = "bluetooth_name"
        const val BLUETOOTH_MAC = "bluetooth_mac"
        const val TOKEN = "token"
    }

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

    fun getBluetoothScanner() : BluetoothScanner? {
        val name = prefs.getString(BLUETOOTH_NAME, "")
        val mac = prefs.getString(BLUETOOTH_MAC, "")

        return if (!name.isNullOrEmpty()
            && !mac.isNullOrEmpty()) {
            BluetoothScanner(name, mac)
        } else null
    }
}
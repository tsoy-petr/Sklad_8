package com.example.sklad_8.data.prefs

data class BluetoothScanner(
    val name: String,
    val mac: String
) {
    override fun toString() = name
}

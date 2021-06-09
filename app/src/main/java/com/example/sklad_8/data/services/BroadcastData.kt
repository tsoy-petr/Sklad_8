package com.example.sklad_8.data.services

import kotlinx.serialization.Serializable

@Serializable
data class BroadcastData(
    var barcode: String = "",
    var error: String = "",
    var isError: Boolean = false,
    var isConnected: Boolean = false,
    var disconnection: Boolean = false,
    var settingsNotMade: Boolean = false
)
package com.example.sklad_8.data.services

data class ScanningData(
    val state: ScanningState
)

sealed class ScanningState() {
    data class Barcode(val data: String): ScanningState()
    data class Error(val message: String): ScanningState()
    object Empty: ScanningState()
    object NewConnection: ScanningState()
    object HasNotBluetoothPermissions: ScanningState()
    object ShowSettingFragment: ScanningState()
    object ScanningClose: ScanningState()
}


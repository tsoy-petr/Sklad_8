package com.example.sklad_8.ui.common

sealed class FetchStatus {
    object Success : FetchStatus()
    object Empty : FetchStatus()
    data class ShowError(val message: String) : FetchStatus()
    object Loading : FetchStatus()
    object Init : FetchStatus()
}

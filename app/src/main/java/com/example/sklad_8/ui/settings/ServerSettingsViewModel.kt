package com.example.sklad_8.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.repositores.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServerSettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    private val _uiSate = MutableStateFlow(
        SettingsServerViewState()
    )

    val uiState = _uiSate.asStateFlow()

    init {
        getDataServer()
    }

    private fun getDataServer() {
        viewModelScope.launch {
            _uiSate.value = repository.getDataServer()
        }
    }

    fun saveServerAddress(address: String) {
        repository.saveServerAddress(address)
    }

    fun saveServerLogin(login: String) {
        repository.saveServerLogin(login)
    }

    fun saveServerPass(pass: String) {
        repository.saveServerPass(pass)
    }

}

data class SettingsServerViewState(
    val serverAddress: String = "",
    val loginServer: String = "",
    val passServer: String = ""
)


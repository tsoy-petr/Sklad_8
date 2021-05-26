package com.example.sklad_8.ui.settings

import androidx.lifecycle.ViewModel
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.repositores.SettingsRepository
import com.example.sklad_8.ui.goods.FetchStatus
import com.example.sklad_8.ui.goods.GoodViewData
import com.example.sklad_8.ui.goods.GoodsViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        _uiSate.value = repository.getDataServer()
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


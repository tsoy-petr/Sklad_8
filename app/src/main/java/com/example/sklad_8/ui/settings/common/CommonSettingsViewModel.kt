package com.example.sklad_8.ui.settings.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.repositores.CommonSettingsRepository
import com.example.sklad_8.ui.common.FetchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CommonSettingsViewModel(
    private val repository: CommonSettingsRepository
) : ViewModel() {

    private val _uiSate = MutableStateFlow(
        CommonSettingsViewState(FetchStatus.Empty)
    )
    val uiState = _uiSate.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fetchStatistic()
            } catch (e: Exception) {
                update {
                    copy(
                        status = FetchStatus.ShowError(e.message.toString())
                    )
                }
            }

        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            update { copy(status = FetchStatus.Loading) }
            repository.deleteAllGoods()
            try {
                fetchStatistic()
            } catch (e: Exception) {
                update {
                    copy(
                        status = FetchStatus.ShowError(e.message.toString())
                    )
                }
            }
        }
    }

    private suspend fun fetchStatistic() {
        update { copy(status = FetchStatus.Loading) }

        coroutineScope {
            val countGoods = async { repository.fetchGoodsCount() }.await()
            val countImgGoods = async { repository.fetchImgGoodsCount() }.await()
            update{
                copy(
                    status = FetchStatus.Success,
                    goodsCount = countGoods,
                    imgGoodsCount = countImgGoods
                )
            }
        }

    }

    private fun update(mapper: CommonSettingsViewState.() -> CommonSettingsViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }

}

data class CommonSettingsViewState(
    val status: FetchStatus,
    val goodsCount: Long = 0,
    val imgGoodsCount: Long = 0
)
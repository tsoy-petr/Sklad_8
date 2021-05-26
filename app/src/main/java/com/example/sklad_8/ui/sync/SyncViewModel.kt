package com.example.sklad_8.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.db.entities.TypeMessage
import com.example.sklad_8.data.repositores.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SyncViewModel(
    private val repository: SyncRepository
) : ViewModel() {

    private var dataJob: Job? = null

    private val _uiSate = MutableStateFlow(
        SyncViewState(SyncFetchStatus.Empty)
    )
    val uiState = _uiSate.asStateFlow()

    fun startSync() {

        if (dataJob?.isActive == true) return

        dataJob = viewModelScope.launch(Dispatchers.IO) {


            update {
                copy(
                    status = SyncFetchStatus.Loading,
                    message = "Начало загрузки данных"
                )
            }
            repository.getGoods().buffer(capacity = 1).collect {
                when (it.type) {
                    TypeMessage.SUCCES -> {
                        dataJob?.cancel()
                        update {
                            copy(status = SyncFetchStatus.Success, message = it.title)
                        }
                    }
                    TypeMessage.LOADING -> {
                        update {
                            copy(status = SyncFetchStatus.Loading, message = it.title)
                        }
                    }
                    TypeMessage.ERROR -> {
                        dataJob?.cancel()
                        update {
                            copy(status = SyncFetchStatus.Error, message = it.title)
                        }
                    }
                }
            }
        }
    }

    private fun update(mapper: SyncViewState.() -> SyncViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }

    override fun onCleared() {
        super.onCleared()
        dataJob?.cancel()
    }

}

data class SyncViewState(
    val status: SyncFetchStatus,
    val message: String = ""
)

sealed class SyncFetchStatus {
    object Empty : SyncFetchStatus()
    object Success : SyncFetchStatus()
    object Loading : SyncFetchStatus()
    object Error : SyncFetchStatus()
}
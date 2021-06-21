package com.example.sklad_8.ui.goods

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.repositores.GoodsRepository
import com.example.sklad_8.data.repositores.data.BarcodeEntity
import com.example.sklad_8.ui.common.FetchStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class DetailGoodViewModel(
    private val repository: GoodsRepository
) : ViewModel() {

    private val _uiSate = MutableStateFlow(
        DetailGoodViewState(
            status = FetchStatus.Empty,
            repostStatus = FetchStatus.Empty,
            barcodeStatus = FetchStatus.Empty
        )
    )
    val uiState = _uiSate.asStateFlow()
    private var job: Job? = null

    fun fetchData(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            async { fetchGood(uuid) }.await()
            async { fetchBarcodes(uuid) }.await()
            async { fetchReport(uuid) }.await()
        }
    }

    private suspend fun fetchReport(uuid: String) {
        update { copy(repostStatus = FetchStatus.Loading) }
        coroutineScope {
            try {
                val htmlString = repository.fetchReport(uuid)
                update {
                    copy(
                        repostStatus = FetchStatus.Success,
                        htmlString = htmlString
                    )
                }
            } catch (e: Exception) {
                update {
                    copy(
                        repostStatus = FetchStatus.ShowError(e.message.toString())
                    )
                }
            }
        }
    }

    private suspend fun fetchBarcodes(uuid: String) {
        update { copy(barcodeStatus = FetchStatus.Loading) }
        coroutineScope {
            try {
                val barcodes = repository.fetchBarcodes(uuid)
                Timber.i("barcodes: $barcodes")
                update {
                    copy(
                        barcodes = barcodes,
                        barcodeStatus = FetchStatus.Success
                    )
                }
            } catch (e: Exception) {
                update {
                    copy(
                        barcodeStatus = FetchStatus.ShowError(e.message.toString())
                    )
                }
            }
        }
    }

    private suspend fun fetchGood(uuid: String) {
        update { copy(status = FetchStatus.Loading) }
        coroutineScope {
            try {
                repository.fetchGoodByUUID(uuid)?.let { good ->
                    Timber.i("good: $good")
                    update {
                        copy(
                            status = FetchStatus.Success,
                            btm = repository.fetchImgMainGood(uuidGood = uuid),
                            title = good.name
                        )
                    }
                }
            } catch (e: Exception) {
                update {
                    copy(
                        status = FetchStatus.ShowError(e.message.toString())
                    )
                }
            }
        }
    }

    private fun update(mapper: DetailGoodViewState.() -> DetailGoodViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}

data class DetailGoodViewState(
    val status: FetchStatus,
    val repostStatus: FetchStatus,
    val barcodeStatus: FetchStatus,
    val btm: Bitmap? = null,
    val title: String = "",
    val barcodes: List<BarcodeEntity> = emptyList(),
    val htmlString: String = ""
)
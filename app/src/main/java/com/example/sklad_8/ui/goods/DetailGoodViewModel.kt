package com.example.sklad_8.ui.goods

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.repositores.GoodsRepository
import com.example.sklad_8.data.repositores.data.BarcodeEntity
import com.example.sklad_8.ui.common.FetchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailGoodViewModel(
    private val repository: GoodsRepository
) : ViewModel() {

    private val _uiSate = MutableStateFlow(
        DetailGoodViewState(status = FetchStatus.Empty)
    )
    val uiState = _uiSate.asStateFlow()

    fun fetchData(uuid: String) {
        update {
            copy(
                status = FetchStatus.Loading
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val good = repository.fetchGoodByUUID(uuid)
            good?.let {
                update {
                    copy(
                        status = FetchStatus.Success,
                        btm = repository.fetchImgMainGood(uuidGood = uuid),
                        title = good.name
                    )
                }
            }
            repository.fetchBarcodes(uuid).apply {
//                Log.i("happy", this.toString())
                update {
                    copy(
                        barcodes = this@apply
                    )
                }
            }
        }
    }

    private fun update(mapper: DetailGoodViewState.() -> DetailGoodViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }
}

data class DetailGoodViewState(
    val status: FetchStatus,
    val btm: Bitmap? = null,
    val title: String = "",
    val barcodes: List<BarcodeEntity> = emptyList()
)
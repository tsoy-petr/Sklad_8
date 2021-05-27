package com.example.sklad_8.ui.goods

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.repositores.GoodsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailGoodViewModel(
    private val repository: GoodsRepository
) : ViewModel() {

    private val _uiSate = MutableStateFlow(
        DetailGoodViewState()
    )
    val uiState = _uiSate.asStateFlow()

    fun fetchData(uuid: String) {
        viewModelScope.launch(Dispatchers.IO)  {
            val good = repository.fetchGoodParentByUUID(uuid, true)
            good?.let {
                _uiSate.value = DetailGoodViewState(
                    btm = good.btmImg,
                    title = good.name
                )
            }
        }
    }
}

data class DetailGoodViewState(
    val btm: Bitmap? = null,
    val title: String = ""
)
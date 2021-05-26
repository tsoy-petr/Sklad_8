package com.example.sklad_8.ui.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.mappers.toUI
import com.example.sklad_8.data.mappers.toUi
import com.example.sklad_8.data.repositores.GoodsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoodsViewModel(
    private val goodsRepository: GoodsRepository
) : ViewModel() {
    private var job: Job? = null

    private val _uiSate = MutableStateFlow(
        GoodsViewState(fetchStatus = FetchStatus.Loading)
    )

    val uiState = _uiSate.asStateFlow()

    private val listHeaderGroups = mapOf<Int, GoodViewData>()

    init {
        fetchGoods()
    }

    private fun update(mapper: GoodsViewState.() -> GoodsViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }


    fun fetchGoods(good: GoodViewData? = null, isFirst: Boolean = false) {

        if (job?.isActive == true) return

        update {
            copy(fetchStatus = FetchStatus.Loading)
        }

        job = viewModelScope.launch(Dispatchers.IO) {
            val data = goodsRepository.fetchGoodsByParent(good, isFirst)
            update {
                copy(
                    fetchStatus = FetchStatus.Success,
                    listGoods = data
                )
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}

data class GoodsViewState(
    val fetchStatus: FetchStatus,
    val listGoods: List<GoodViewData> = emptyList(),
    val currentGood: GoodEntity? = null
)

sealed class FetchStatus {
    object Success : FetchStatus()
    object Empty : FetchStatus()
    data class ShowError(val message: String) : FetchStatus()
    object Loading : FetchStatus()
}
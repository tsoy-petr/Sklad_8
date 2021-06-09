package com.example.sklad_8.ui.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.repositores.GoodsRepository
import com.example.sklad_8.ui.common.FetchStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class GoodsViewModel(
    private val goodsRepository: GoodsRepository
) : ViewModel() {

    private var curGoodViewData: GoodViewData? = null
    private var curIsFirst: Boolean = false

    private var job: Job? = null

    private val _uiSate = MutableStateFlow(
        GoodsViewState(fetchStatus = FetchStatus.Init)
    )

    val uiState = _uiSate.asStateFlow().filterNotNull()

    private val listHeaderGroups = mutableListOf<GoodViewData>()

    private fun update(mapper: GoodsViewState.() -> GoodsViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }


    fun fetchGoods(good: GoodViewData? = null, isFirst: Boolean = false, isReload:Boolean = false) {

        good?.let {
            if (!isReload && good.id == curGoodViewData?.id) {
                return@fetchGoods
            }
        }

        if (job?.isActive == true) return

        curGoodViewData = good
        curIsFirst = isFirst

        update {
            copy(fetchStatus = FetchStatus.Loading)
        }

        job = viewModelScope.launch(Dispatchers.IO) {
            listHeaderGroups.clear()
            val data = goodsRepository.fetchGoodsByParent(good, isFirst)
            good?.let {
                data.forEach {
                    if (it.isGroup && it.isHeader) listHeaderGroups.add(it)
                }
            }
            update {
                copy(
                    fetchStatus = FetchStatus.Success,
                    listGoods = data
                )
            }
        }
    }

    fun refreshData() {
        fetchGoods(curGoodViewData, curIsFirst, true)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun isHeader(good: GoodViewData) =
        listHeaderGroups
            .find {
                it.id == good.id
            }?.let { true } ?: false
}

data class GoodsViewState(
    val fetchStatus: FetchStatus,
    val listGoods: List<GoodViewData> = emptyList(),
    val currentGood: GoodEntity? = null
)


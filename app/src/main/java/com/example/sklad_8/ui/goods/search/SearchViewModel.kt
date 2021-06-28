package com.example.sklad_8.ui.goods.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sklad_8.data.db.entities.GoodEntity
import com.example.sklad_8.data.mappers.toUi
import com.example.sklad_8.data.repositores.SearchRepository
import com.example.sklad_8.ui.common.FetchStatus
import com.example.sklad_8.ui.goods.GoodViewData
import com.example.sklad_8.ui.goods.GoodsViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiSate = MutableStateFlow(
        GoodsSearchViewState(fetchStatus = FetchStatus.Init)
    )
    val uiState = _uiSate.asStateFlow().filterNotNull()

    private var job: Job? = null

    fun findByName(searchText: String) {

        if (searchText.length < 3 && searchText.length != 0) return

        update { copy(fetchStatus = FetchStatus.Loading) }

        if (job?.isActive == true) job?.cancel()

        job = viewModelScope.launch(Dispatchers.IO) {
            val data = searchRepository.findGoodsByNameFullText(searchText)
            if (data.isNotEmpty()) {
                update {
                    copy(
                        fetchStatus = FetchStatus.Success,
                        listGoods = data.toUi()
                    )
                }
            } else {
                update { copy(fetchStatus = FetchStatus.Empty) }
            }
        }

    }

    private fun update(mapper: GoodsSearchViewState.() -> GoodsSearchViewState = { this }) {
        val data = _uiSate.value.mapper()
        _uiSate.value = data
    }

}

data class GoodsSearchViewState(
    val fetchStatus: FetchStatus,
    val listGoods: List<GoodViewData> = emptyList()
)
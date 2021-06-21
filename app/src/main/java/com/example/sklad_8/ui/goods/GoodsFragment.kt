package com.example.sklad_8.ui.goods

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sklad_8.App
import com.example.sklad_8.R
import com.example.sklad_8.Screens
import com.example.sklad_8.ui.common.FetchStatus
import com.github.terrakok.modo.backTo
import com.github.terrakok.modo.backToTabRoot
import com.github.terrakok.modo.forward
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoodsFragment : Fragment(R.layout.fragment_goods) {

    private lateinit var rvGoods: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val goodsViewModel: GoodsViewModel by viewModel()

    private val modo = App.modo
    private val tabId: Int by lazy { requireArguments().getInt(ARG_TAB_ID) }
    private val screenId: Int by lazy { requireArguments().getInt(ARG_ID) }

    private lateinit var goodsAdapter: GoodsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGoods = view.findViewById(R.id.rv_goods)
        swipeRefreshLayout = view.findViewById(R.id.sw_ref_goods)
        swipeRefreshLayout.setOnRefreshListener {
            goodsViewModel.refreshData()
        }
        setupGoodsAdapter()
        lifecycleScope.launchWhenStarted {
            goodsViewModel.uiState.collect {
                swipeRefreshLayout.isRefreshing = it.fetchStatus is FetchStatus.Loading
                acceptUiState(it)
            }
        }
    }

    private fun acceptUiState(state: GoodsViewState) {
        when (state.fetchStatus) {
            is FetchStatus.Success -> goodsAdapter.submitList(state.listGoods)
            is FetchStatus.Init -> {
                arguments?.apply {
                    val good: GoodViewData? = this.getParcelable(ARG_GOOD_VIEW_DATA)
                    val isFirst = this.getBoolean(ARG_IS_FIRST)
                    val newGood = good?.copy(tabId = tabId, screenId = screenId)
                    goodsViewModel.fetchGoods(
                        newGood,
                        isFirst
                    )
                }
            }
            else -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.goods_menu, menu)
//        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.searchGood -> {
                modo.forward(Screens.SearchGoodsScreen())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupGoodsAdapter() {
        goodsAdapter = GoodsAdapter(::onClickItem)
        rvGoods.apply {
            adapter = goodsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onClickItem(good: GoodViewData, isFirst: Boolean) {
        if (good.isGroup) {
            if (goodsViewModel.isHeader(good)) {
                if (good.parent.isEmpty()) {
                    modo.backToTabRoot()
                } else {
                    modo.backTo("${good.tabId}:${good.screenId}")
                }
            } else {
                modo.forward(Screens.GoodsScreen(tabId, screenId + 1, good, isFirst))
            }
        } else {
            modo.forward(Screens.DetailGoodScreen(good.id))
        }
    }

    companion object {
        private const val ARG_GOOD_VIEW_DATA = "good_view_data"
        private const val ARG_IS_FIRST = "is_first"
        private const val ARG_ID = "arg_id"
        private const val ARG_TAB_ID = "arg_tab_id"
        fun create(tabId: Int, id: Int, good: GoodViewData?, isFirst: Boolean) =
            GoodsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TAB_ID, tabId)
                    putInt(ARG_ID, id)
                    good?.let { putParcelable(ARG_GOOD_VIEW_DATA, good) }
                    putBoolean(ARG_IS_FIRST, isFirst)
                }
            }
    }

}
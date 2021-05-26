package com.example.sklad_8.ui.goods

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sklad_8.App
import com.example.sklad_8.R
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GoodsFragment : Fragment(R.layout.fragment_goods) {

    //    private val binding by viewBinding(FragmentGoodsBinding::bind)
    private lateinit var rvGoods: RecyclerView

    private val goodsViewModel: GoodsViewModel by sharedViewModel()

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

        setupGoodsAdapter()
        lifecycleScope.launchWhenCreated {
            goodsViewModel.uiState.collect {
                when (it.fetchStatus) {
                    is FetchStatus.Success -> goodsAdapter.submitList(it.listGoods)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.goods_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return true

    }

    private fun setupGoodsAdapter() {
        goodsAdapter = GoodsAdapter { item, isFirst ->
            if (item.isGroup) {
                    goodsViewModel.fetchGoods(item, isFirst)
            }
        }
        rvGoods.apply {
            adapter = goodsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TAB_ID = "arg_tab_id"
        fun create(tabId: Int, id: Int) = GoodsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TAB_ID, tabId)
                putInt(ARG_ID, id)
            }
        }
    }

}
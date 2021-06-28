package com.example.sklad_8.ui.goods.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.App
import com.example.sklad_8.R
import com.example.sklad_8.Screens
import com.example.sklad_8.databinding.FragmentSearchGoodsBinding
import com.example.sklad_8.ui.common.FetchStatus
import com.example.sklad_8.ui.goods.GoodViewData
import com.example.sklad_8.ui.goods.GoodsAdapter
import com.example.sklad_8.ui.util.hideKeyboard
import com.github.terrakok.modo.back
import com.github.terrakok.modo.forward
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchGoodsFragment : Fragment(R.layout.fragment_search_goods) {

    private val modo = App.modo
    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    private val viewModel: SearchViewModel by viewModel()
    private val binding: FragmentSearchGoodsBinding by viewBinding()

    private var goodsAdapter: GoodsAdapter? = null

    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.apply {
            searchText = getString(KEY_CURR_SEARCH_TEXT).toString()
        }

        setupGoodsAdapter()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when(state.fetchStatus){
                        is FetchStatus.Success -> goodsAdapter?.submitList(state.listGoods)
                        else -> {goodsAdapter?.submitList(emptyList())}
                    }
                }
            }
        }
    }

    private fun setupGoodsAdapter() {
        goodsAdapter = GoodsAdapter(::onClickItem)
        binding.rvSearchGoods.apply {
            adapter = goodsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onClickItem(good: GoodViewData, isFirst: Boolean) {
        modo.forward(Screens.DetailGoodScreen(good.id))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_good_menu, menu)

        val searchItem = menu.findItem(R.id.action_second)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }

        searchItem.expandActionView()
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                modo.back()
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
        searchView?.setQuery(searchText, false)

        searchView?.apply {
            val searchPlate = searchView?.findViewById<LinearLayout>(R.id.search_plate)
            val mSearchEditText = searchPlate?.findViewById<EditText>(R.id.search_src_text)
            mSearchEditText?.hint = context.getString(R.string.search_title)
            this.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (lifecycle.currentState == Lifecycle.State.RESUMED){
                            searchText = it
                        }
                        Timber.i("onQueryTextChange: $it")
                        viewModel.findByName(searchText)
                    }
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        searchText = it
                        viewModel.findByName(it)
                    }
                    //Убераем курсор со searchView
                    mSearchEditText?.apply {
                        clearFocus()
                    }
                    //скрываем клавиатуру
                    searchView?.hideKeyboard()
                    return false
                }
            }
            this.setOnQueryTextListener(queryTextListener)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_second -> {
                searchView?.setOnQueryTextListener(queryTextListener)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchView?.apply {
            outState.putString(KEY_CURR_SEARCH_TEXT, query.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchView = null
        queryTextListener = null
        goodsAdapter = null
    }

    companion object {

        private const val KEY_CURR_SEARCH_TEXT = "curr_search_text"

        fun create(): SearchGoodsFragment {
            return SearchGoodsFragment()
        }
    }

}

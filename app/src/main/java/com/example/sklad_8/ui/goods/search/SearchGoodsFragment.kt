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
import com.example.sklad_8.App
import com.example.sklad_8.R
import com.example.sklad_8.ui.util.hideKeyboard
import com.github.terrakok.modo.back
import timber.log.Timber

class SearchGoodsFragment : Fragment(R.layout.fragment_search_goods) {

    private val modo = App.modo
    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.search_good_menu, menu)

        val searchItem = menu.findItem(R.id.action_second)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.apply {

            val searchPlate = searchView?.findViewById<LinearLayout>(R.id.search_plate)
            val mSearchEditText = searchPlate?.findViewById<EditText>(R.id.search_src_text)
            mSearchEditText?.hint = context.getString(R.string.search_title)
            this.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    Timber.i("onQueryTextChange")
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    Timber.i("onQueryTextSubmit")

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

    companion object {
        fun create(): SearchGoodsFragment {
            return SearchGoodsFragment()
        }
    }

}

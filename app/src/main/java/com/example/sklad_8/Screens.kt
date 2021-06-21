package com.example.sklad_8

import com.example.sklad_8.ui.goods.DetailGoodFragment
import com.example.sklad_8.ui.goods.GoodViewData
import com.example.sklad_8.ui.goods.GoodsFragment
import com.example.sklad_8.ui.goods.GoodsFragment.Companion.create
import com.example.sklad_8.ui.goods.search.SearchGoodsFragment
import com.example.sklad_8.ui.settings.SettingsFragment
import com.example.sklad_8.ui.sync.SyncFragment
import com.example.sklad_8.ui.sync.work.SyncWorkFragment
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.MultiAppScreen
import kotlinx.android.parcel.Parcelize

object Screens {

    @Parcelize
    class GoodsScreen(private val tabId: Int, private val i: Int, val good: GoodViewData?, val isFirst: Boolean = false) : AppScreen("${tabId}:$i") {
        override fun create() = GoodsFragment.create(tabId, i, good, isFirst)
    }

    @Parcelize
    class SearchGoodsScreen() : AppScreen("SearchGoods") {
        override fun create() = SearchGoodsFragment.create()
    }

    @Parcelize
    class DetailGoodScreen(private val uuid: String) : AppScreen(uuid) {
        override fun create() = DetailGoodFragment.create(uuid)
    }

    @Parcelize
    class SyncScreen(private val tabId: Int, private val i: Int) : AppScreen("${tabId}:$i") {
//        override fun create() = SyncFragment.create(tabId, i)
        override fun create() = SyncWorkFragment()
    }

    @Parcelize
    class SettingScreen() : AppScreen("Settings") {
        override fun create() = SettingsFragment()
    }

    fun MultiStack() = MultiAppScreen(
        "MultiStack",
        listOf(GoodsScreen(0, 1, null, false), SyncScreen(1, 1)),
        0
    )

}
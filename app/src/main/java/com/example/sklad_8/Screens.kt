package com.example.sklad_8

import com.example.sklad_8.ui.goods.DetailGoodFragment
import com.example.sklad_8.ui.goods.GoodsFragment
import com.example.sklad_8.ui.settings.SettingsFragment
import com.example.sklad_8.ui.sync.SyncFragment
import com.github.terrakok.modo.android.AppScreen
import com.github.terrakok.modo.android.MultiAppScreen
import kotlinx.android.parcel.Parcelize

object Screens {

    @Parcelize
    class GoodsScreen(private val tabId: Int, private val i: Int) : AppScreen("${tabId}:$i") {
        override fun create() = GoodsFragment.create(tabId, i)
    }

    @Parcelize
    class DetailGoodScreen(private val uuid: String) : AppScreen(uuid) {
        override fun create() = DetailGoodFragment.create(uuid)
    }

    @Parcelize
    class SyncScreen(private val tabId: Int, private val i: Int) : AppScreen("${tabId}:$i") {
        override fun create() = SyncFragment.create(tabId, i)
    }

    @Parcelize
    class SettingScreen() : AppScreen("Settings") {
        override fun create() = SettingsFragment()
    }

    fun MultiStack() = MultiAppScreen(
        "MultiStack",
        listOf(GoodsScreen(0, 1), SyncScreen(1, 1)),
        0
    )

}
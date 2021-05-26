package com.example.sklad_8.ui.settings

import android.os.Bundle
import android.text.BoringLayout
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.sklad_8.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var viewPager: ViewPager2
    lateinit var tabLayout: TabLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager = view.findViewById(R.id.viewPager2)
        tabLayout = view.findViewById(R.id.tabLayout)

        init()
    }

    private fun init() {

        viewPager.adapter = SettingsFragmentStateAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    "Настройки сервера"
                }
                1 -> {
                    "Настройки сканера"
                }
                else -> {
                    "ERROR"
                }
            }
        }.attach()
    }

}
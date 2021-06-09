package com.example.sklad_8.ui.settings

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sklad_8.ui.settings.common.CommonSettingsFragment

class SettingsFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {ServerSettingsFragment()}
            1 -> {BluetoothSettingsFragment()}
            2 -> {
                CommonSettingsFragment()
            }
            else -> ServerSettingsFragment()
        }
    }
}
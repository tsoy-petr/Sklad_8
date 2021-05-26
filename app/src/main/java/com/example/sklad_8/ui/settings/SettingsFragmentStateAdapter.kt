package com.example.sklad_8.ui.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SettingsFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {ServerSettingsFragment()}
            1 -> {BluetoothSettingsFragment()}
            else -> ServerSettingsFragment()
        }
    }
}
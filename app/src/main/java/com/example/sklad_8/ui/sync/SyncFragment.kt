package com.example.sklad_8.ui.sync

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.App
import com.example.sklad_8.R
import com.example.sklad_8.databinding.FragmentSyncBinding
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SyncFragment : Fragment(R.layout.fragment_sync) {

    private val syncViewModel: SyncViewModel by sharedViewModel()

//    private val binding by viewBinding(FragmentSyncBinding::bind)
    private lateinit var tvSync: TextView
    private lateinit var button: Button

    private val modo = App.modo
    private val tabId: Int by lazy { requireArguments().getInt(ARG_TAB_ID) }
    private val screenId: Int by lazy { requireArguments().getInt(ARG_ID) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvSync = view.findViewById(R.id.tv_sync)
        button = view.findViewById(R.id.button)

        lifecycleScope.launchWhenCreated {
            syncViewModel.uiState.collect {
                tvSync.text = it.message
            }
        }
        button.setOnClickListener { syncViewModel.startSync() }
    }

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TAB_ID = "arg_tab_id"
        fun create(tabId: Int, id: Int) = SyncFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_TAB_ID, tabId)
                putInt(ARG_ID, id)
            }
        }
    }

}
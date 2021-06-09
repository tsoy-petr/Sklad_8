package com.example.sklad_8.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.R
import com.example.sklad_8.databinding.FragmentServerSettingsBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.*
import timber.log.Timber

class ServerSettingsFragment : Fragment(R.layout.fragment_server_settings) {

    private val binding: FragmentServerSettingsBinding by viewBinding()
    private val viewModel: ServerSettingsViewModel by viewModel()

    private val textWatcherServerAddress by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.saveServerAddress(s.toString())
                }
            }
        }
    }

    private val textWatcherServerLogin by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    viewModel.saveServerLogin(s.toString())
                }
            }
        }
    }

    private val textWatcherServerPass by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let { viewModel.saveServerPass(s.toString()) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etServerAddress.addTextChangedListener(textWatcherServerAddress)
        binding.etServerLogin.addTextChangedListener(textWatcherServerLogin)
        binding.etServerPass.addTextChangedListener(textWatcherServerPass)

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                binding.etServerAddress.setText(it.serverAddress)
                binding.etServerLogin.setText(it.loginServer)
                binding.etServerPass.setText(it.passServer)
            }
        }

    }
}
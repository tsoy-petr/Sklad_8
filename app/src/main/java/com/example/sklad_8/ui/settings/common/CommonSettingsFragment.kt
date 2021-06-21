package com.example.sklad_8.ui.settings.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.R
import com.example.sklad_8.databinding.FragmentCommonSettingsBinding
import com.example.sklad_8.ui.common.FetchStatus
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommonSettingsFragment : Fragment(R.layout.fragment_common_settings) {

    private val viewModel: CommonSettingsViewModel by viewModel()
    private val binding: FragmentCommonSettingsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDelAllData.setOnClickListener {
            viewModel.deleteAllData()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { viewState ->

                when (viewState.status) {
                    is FetchStatus.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.group.visibility = View.GONE
                    }
                    is FetchStatus.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.group.visibility = View.VISIBLE
                    }
                    is FetchStatus.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.group.visibility = View.VISIBLE
                        binding.txtCountGoods.text = "Номенклатура(${viewState.goodsCount})"
                        binding.txtImgCount.text = "Картинки номенклатуры(${viewState.imgGoodsCount})"
                    }
                    is FetchStatus.ShowError -> {
                        binding.progressBar.visibility = View.GONE
                        binding.group.visibility = View.VISIBLE
//                        Toast.makeText(
//                            this@CommonSettingsFragment,
//                            viewState.status.message,
//                            Toast.LENGTH_SHORT
//                        )
                    }
                }

            }
        }
    }
}
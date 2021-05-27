package com.example.sklad_8.ui.goods

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.R
import com.example.sklad_8.databinding.FragmentDetailGoodBinding
import com.example.sklad_8.databinding.FragmentServerSettingsBinding
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailGoodFragment : Fragment(R.layout.fragment_detail_good) {

    private val uuid_id: String by lazy { requireArguments().getString(UUID_ID) ?: "" }
    private val viewModel: DetailGoodViewModel by viewModel()
    private val binding: FragmentDetailGoodBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchData(uuid_id)

        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect {
                binding.txtTitle.text = it.title
                binding.imageView.setImageBitmap(it.btm)
            }
        }

    }

    companion object {
        private const val UUID_ID = "uuid_id"
        fun create(uuid: String) = DetailGoodFragment().apply {
            arguments = Bundle().apply {
                putString(UUID_ID, uuid)
            }
        }
    }

}
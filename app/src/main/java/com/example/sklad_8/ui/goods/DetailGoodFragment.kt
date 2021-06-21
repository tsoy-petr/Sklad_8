package com.example.sklad_8.ui.goods

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.sklad_8.R
import com.example.sklad_8.databinding.FragmentDetailGoodBinding
import com.example.sklad_8.ui.adapter.BarcodeAdapter
import com.example.sklad_8.ui.common.FetchStatus
import com.example.sklad_8.ui.util.SpeedyLinearLayoutManager
import com.example.sklad_8.ui.util.StartSnapHelper
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailGoodFragment : Fragment(R.layout.fragment_detail_good) {

    private val uuid_id: String by lazy { requireArguments().getString(UUID_ID) ?: "" }
    private val viewModel: DetailGoodViewModel by viewModel()
    private val binding: FragmentDetailGoodBinding by viewBinding()
    private var startSnapHelper: StartSnapHelper? = null

    private lateinit var barcodeAdapter: BarcodeAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.fetchData(uuid_id)

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { viewState ->
                when (viewState.status) {
                    is FetchStatus.Loading -> binding.pbLoadDetailGood.visibility = View.VISIBLE
                    else -> {
                        binding.pbLoadDetailGood.visibility = View.GONE
                        binding.txtTitle.text = viewState.title
                        binding.imageView.setImageBitmap(viewState.btm)
                        barcodeAdapter.submitList(viewState.barcodes)

                    }
                }
                when(viewState.repostStatus) {
                    is FetchStatus.Success -> {
                        binding.wvReport.visibility = View.VISIBLE
                        binding.wvReport.loadDataWithBaseURL(
                            null, viewState.htmlString,
                            "text/html",
                            "utf-8",
                            null
                        )
                    }
                    else -> {
                        binding.wvReport.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() {

        barcodeAdapter = BarcodeAdapter()

        binding.rvBarcode.apply {
            adapter = barcodeAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            val decorator = DividerItemDecoration(requireContext(), LinearLayout.HORIZONTAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.divider_drawable)?.let {
                decorator.setDrawable(it)
            }
            addItemDecoration(decorator)

        }
        startSnapHelper = StartSnapHelper()
        startSnapHelper?.attachToRecyclerView(binding.rvBarcode)
    }

    override fun onDestroy() {
        super.onDestroy()
        startSnapHelper = null
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
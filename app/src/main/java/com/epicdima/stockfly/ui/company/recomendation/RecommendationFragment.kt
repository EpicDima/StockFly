package com.epicdima.stockfly.ui.company.recomendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentRecommendationBinding
import com.epicdima.stockfly.other.setArgument
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class RecommendationFragment :
    BaseViewModelFragment<RecommendationViewModel, FragmentRecommendationBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_recommendations"

        @JvmStatic
        fun newInstance(ticker: String): RecommendationFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return RecommendationFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: RecommendationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.progressBarWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.errorWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        setupSlider()
        setupObservers()
    }

    private fun setupSlider() {
        binding.periodSlider.addOnChangeListener { slider, _, _ ->
            viewModel.beginIndex = slider.values.first().toInt()
            viewModel.endIndex = slider.values.last().toInt()
        }
    }

    private fun setupObservers() {
        viewModel.periodRange
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach {
                binding.apply {
                    periodSlider.apply {
                        stepSize = 1.0f
                        valueFrom = 0.0f
                        valueTo = (viewModel.length - 1).toFloat()
                        values =
                            listOf(viewModel.beginIndex.toFloat(), viewModel.endIndex.toFloat())
                    }
                    beginDate.text = it.first
                    endDate.text = it.second
                    recommendationView.updateData(viewModel.recommendations, viewModel.brandNewData)
                    checkVisibility()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun checkVisibility() {
        binding.recommendationView.isVisible =
            viewModel.loading.value != true && viewModel.error.value != true
        binding.periodSlider.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value == true || viewModel.error.value == true)
        binding.beginDate.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value == true || viewModel.error.value == true)
        binding.endDate.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value == true || viewModel.error.value == true)
    }
}
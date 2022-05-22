package com.epicdima.stockfly.feature.details.recommendations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.core.common.ViewModelFragment
import com.epicdima.stockfly.core.common.setArgument
import com.epicdima.stockfly.core.formatter.Formatter
import com.epicdima.stockfly.core.formatter.model.periodFormatted
import com.epicdima.stockfly.feature.details.recommendations.databinding.FragmentRecommendationsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RecommendationsFragment :
    ViewModelFragment<RecommendationViewModel, FragmentRecommendationsBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_recommendations"

        @JvmStatic
        fun newInstance(ticker: String): RecommendationsFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return RecommendationsFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: RecommendationViewModel by viewModels()

    @Inject
    lateinit var formatter: Formatter

    override fun getLayoutId(): Int = R.layout.fragment_recommendations

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentRecommendationsBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentRecommendationsBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recommendationsView.setFormatter(formatter)
        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.progressBarWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
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
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .filterNotNull()
            .onEach {
                binding.apply {
                    if (viewModel.length > 0) {
                        periodSlider.apply {
                            stepSize = 1.0f
                            valueFrom = 0.0f
                            valueTo = (viewModel.length - 1).toFloat()

                            if (valueTo - valueFrom < 1.0f) {
                                valueTo = 1.0f
                            }

                            values = listOf(
                                viewModel.beginIndex.toFloat(),
                                viewModel.endIndex.toFloat()
                            )
                        }
                    }
                    beginDate.text = it.first.periodFormatted(formatter)
                    endDate.text = it.second.periodFormatted(formatter)
                    recommendationsView.updateData(
                        viewModel.recommendations.toList(),
                        viewModel.brandNewData
                    )
                    checkVisibility()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun checkVisibility() {
        binding.recommendationsView.isVisible =
            viewModel.loading.value != true && viewModel.error.value != true
        binding.periodSlider.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value || viewModel.error.value)
        binding.beginDate.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value || viewModel.error.value)
        binding.endDate.isVisible =
            !(viewModel.recommendations.isEmpty() || viewModel.loading.value || viewModel.error.value)
    }
}
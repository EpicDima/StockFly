package com.epicdima.stockfly.ui.company.recomendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentRecommendationBinding
import com.epicdima.stockfly.other.setArgument
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

    private val initialObserver: Observer<Pair<String, String>> by lazy {
        Observer<Pair<String, String>> {
            binding.periodSlider.apply {
                stepSize = 1.0f
                valueFrom = 0.0f
                valueTo = (viewModel.length - 1).toFloat()
                values = listOf(viewModel.beginIndex.toFloat(), viewModel.endIndex.toFloat())
            }
            viewModel.periodRange.removeObserver(initialObserver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            error = viewModel.error
            empty = true
        }
        setupSlider()
        setupObservers()
        return binding.root
    }

    private fun setupSlider() {
        binding.periodSlider.addOnChangeListener { slider, _, _ ->
            viewModel.beginIndex = slider.values.first().toInt()
            viewModel.endIndex = slider.values.last().toInt()
        }
    }

    private fun setupObservers() {
        viewModel.periodRange.observe(viewLifecycleOwner, initialObserver)
        viewModel.periodRange.observe(viewLifecycleOwner) {
            binding.apply {
                beginDate.text = it.first
                endDate.text = it.second
                recommendationView.updateData(viewModel.recommendations, viewModel.brandNewData)
                binding.empty = viewModel.recommendations.isEmpty()
            }
        }
    }
}
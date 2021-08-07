package com.epicdima.stockfly.ui.company.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentChartBinding
import com.epicdima.stockfly.other.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class ChartFragment : BaseViewModelFragment<ChartViewModel, FragmentChartBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_chart"

        @JvmStatic
        fun newInstance(ticker: String): ChartFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return ChartFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: ChartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.chart.visibility = if (it) View.INVISIBLE else View.VISIBLE
                binding.progressBar.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.company
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach {
                binding.apply {
                    current.text = it.currentString
                    val changeText = it.changeString + (if (it.changePercentString.isEmpty()) "" else " (") + it.changePercentString + (if (it.changePercentString.isEmpty()) "" else ")")
                    change.text = changeText
                    change.setTextColor(
                        getColor(
                            when {
                                it.changeString.startsWith("+") -> R.color.green
                                it.changeString.startsWith("-") -> R.color.red
                                else -> R.color.black
                            }
                        )
                    )
                    buyButton.text = getString(R.string.buy_stock, it.currentString)
                    buyButton.visibility = if (it.quote != null) View.VISIBLE else View.INVISIBLE
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setupDateButtons()
        setupBuyButton()
        setupObservers()
    }

    private fun setupDateButtons() {
        binding.dayButton.setOnClickListener { viewModel.dayChart() }
        binding.weekButton.setOnClickListener { viewModel.weekChart() }
        binding.monthButton.setOnClickListener { viewModel.monthChart() }
        binding.sixMonthsButton.setOnClickListener { viewModel.sixMonthsChart() }
        binding.yearButton.setOnClickListener { viewModel.yearChart() }
        binding.allTimeButton.setOnClickListener { viewModel.allTimeChart() }
    }

    private fun setupBuyButton() {
        binding.buyButton.setOnClickListener {
            viewModel.company.value?.let { company ->
                if (company.quote != null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.bought_stock, company.ticker, company.currentString),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.stockCandles
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.chart.updateData(it, viewModel.brandNewData, viewModel.brandNewData)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stockCandleParam
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                unselectButtonByParam(viewModel.previousStockCandleParam)
                selectButtonByParam(it)
                binding.chart.updateFormat(it.format)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun unselectButtonByParam(stockCandleParam: StockCandleParam?) {
        Timber.v("unselect button %s", stockCandleParam)
        when (stockCandleParam) {
            StockCandleParam.DAY -> unselectButton(binding.dayButton)
            StockCandleParam.WEEK -> unselectButton(binding.weekButton)
            StockCandleParam.MONTH -> unselectButton(binding.monthButton)
            StockCandleParam.SIX_MONTHS -> unselectButton(binding.sixMonthsButton)
            StockCandleParam.YEAR -> unselectButton(binding.yearButton)
            StockCandleParam.ALL_TIME -> unselectButton(binding.allTimeButton)
        }
    }

    private fun selectButtonByParam(stockCandleParam: StockCandleParam) {
        Timber.i("select button %s", stockCandleParam)
        when (stockCandleParam) {
            StockCandleParam.DAY -> selectButton(binding.dayButton)
            StockCandleParam.WEEK -> selectButton(binding.weekButton)
            StockCandleParam.MONTH -> selectButton(binding.monthButton)
            StockCandleParam.SIX_MONTHS -> selectButton(binding.sixMonthsButton)
            StockCandleParam.YEAR -> selectButton(binding.yearButton)
            StockCandleParam.ALL_TIME -> selectButton(binding.allTimeButton)
        }
    }

    private fun unselectButton(button: TextView) {
        button.apply {
            setTextColor(context.color(R.color.black))
            background = context.drawable(R.drawable.button_datetime_background)
        }
    }

    private fun selectButton(button: TextView) {
        button.apply {
            setTextColor(context.color(R.color.white))
            background = context.drawable(R.drawable.button_datetime_selected_background)
        }
    }
}
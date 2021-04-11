package ru.yandex.stockfly.ui.company.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentChartBinding
import ru.yandex.stockfly.other.StockCandleParam
import ru.yandex.stockfly.other.color
import ru.yandex.stockfly.other.drawable
import ru.yandex.stockfly.other.setArgument

@AndroidEntryPoint
class ChartFragment : BaseFragment<ChartViewModel, FragmentChartBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_chart"

        @JvmStatic
        fun newInstance(ticker: String): ChartFragment {
            return ChartFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: ChartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            company = viewModel.company
        }
        setupDateButtons()
        setupBuyButton()
        setupObservers()
        return binding.root
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
        viewModel.stockCandles.observe(viewLifecycleOwner) {
            binding.chart.updateData(it, viewModel.brandNewData)
        }
        viewModel.stockCandleParam.observe(viewLifecycleOwner) {
            unselectButtonByParam(viewModel.previousStockCandleParam)
            selectButtonByParam(it)
            binding.chart.updateFormat(it.format)
        }
    }

    private fun unselectButtonByParam(stockCandleParam: StockCandleParam?) {
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
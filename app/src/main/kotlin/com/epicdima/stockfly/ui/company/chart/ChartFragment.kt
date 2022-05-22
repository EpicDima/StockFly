package com.epicdima.stockfly.ui.company.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.R
import com.epicdima.stockfly.core.common.ViewModelFragment
import com.epicdima.stockfly.core.common.setArgument
import com.epicdima.stockfly.core.formatter.model.changePercentString
import com.epicdima.stockfly.core.formatter.model.changeString
import com.epicdima.stockfly.core.formatter.model.currentString
import com.epicdima.stockfly.core.formatter.model.format
import com.epicdima.stockfly.core.ui.color
import com.epicdima.stockfly.core.ui.drawable
import com.epicdima.stockfly.core.ui.getColor
import com.epicdima.stockfly.databinding.FragmentChartBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChartFragment : ViewModelFragment<ChartViewModel, FragmentChartBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_chart"

        @JvmStatic
        fun newInstance(ticker: String): ChartFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return ChartFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: ChartViewModel by viewModels()

    @Inject
    lateinit var formatter: com.epicdima.stockfly.core.formatter.Formatter

    override fun getLayoutId(): Int = R.layout.fragment_chart

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentChartBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentChartBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.chart.visibility = if (it) View.INVISIBLE else View.VISIBLE
                binding.progressBar.isVisible = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.company
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .filterNotNull()
            .onEach {
                binding.apply {
                    current.text = it.currentString
                    val changeText =
                        it.changeString + (if (it.changePercentString(formatter)
                                .isEmpty()
                        ) "" else " (") + it.changePercentString(formatter) + (if (it.changePercentString(
                                formatter
                            ).isEmpty()
                        ) "" else ")")
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
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.chart.updateData(it, viewModel.brandNewData, viewModel.brandNewData)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.stockCandleParam
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .filterNotNull()
            .onEach {
                unselectButtonByParam(viewModel.previousStockCandleParam)
                selectButtonByParam(it)
                binding.chart.updateFormat(it.format(formatter))
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun unselectButtonByParam(stockCandleParam: com.epicdima.stockfly.core.model.StockCandleParam?) {
        Timber.v("unselect button %s", stockCandleParam)
        when (stockCandleParam) {
            com.epicdima.stockfly.core.model.StockCandleParam.DAY -> unselectButton(binding.dayButton)
            com.epicdima.stockfly.core.model.StockCandleParam.WEEK -> unselectButton(binding.weekButton)
            com.epicdima.stockfly.core.model.StockCandleParam.MONTH -> unselectButton(binding.monthButton)
            com.epicdima.stockfly.core.model.StockCandleParam.SIX_MONTHS -> unselectButton(binding.sixMonthsButton)
            com.epicdima.stockfly.core.model.StockCandleParam.YEAR -> unselectButton(binding.yearButton)
            com.epicdima.stockfly.core.model.StockCandleParam.ALL_TIME -> unselectButton(binding.allTimeButton)
            else -> {}
        }
    }

    private fun selectButtonByParam(stockCandleParam: com.epicdima.stockfly.core.model.StockCandleParam) {
        Timber.i("select button %s", stockCandleParam)
        when (stockCandleParam) {
            com.epicdima.stockfly.core.model.StockCandleParam.DAY -> selectButton(binding.dayButton)
            com.epicdima.stockfly.core.model.StockCandleParam.WEEK -> selectButton(binding.weekButton)
            com.epicdima.stockfly.core.model.StockCandleParam.MONTH -> selectButton(binding.monthButton)
            com.epicdima.stockfly.core.model.StockCandleParam.SIX_MONTHS -> selectButton(binding.sixMonthsButton)
            com.epicdima.stockfly.core.model.StockCandleParam.YEAR -> selectButton(binding.yearButton)
            com.epicdima.stockfly.core.model.StockCandleParam.ALL_TIME -> selectButton(binding.allTimeButton)
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
package com.epicdima.stockfly.ui.company.chart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.core.common.DownloadableViewModel
import com.epicdima.stockfly.core.data.Repository
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.core.model.StockCandleParam
import com.epicdima.stockfly.core.model.StockCandles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(ChartFragment.TICKER_KEY)!!

    private var _stockCandleParam = MutableStateFlow<StockCandleParam?>(null)
    val stockCandleParam: StateFlow<StockCandleParam?> = _stockCandleParam.asStateFlow()

    private var _previousStockCandleParam: StockCandleParam? = null
    val previousStockCandleParam: StockCandleParam?
        get() = _previousStockCandleParam

    val company: StateFlow<Company?> = repository.getCompany(ticker)
        .onEach { updateChart(repeat = true) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _stockCandles = MutableStateFlow<StockCandles?>(null)
    val stockCandles: StateFlow<StockCandles?> = _stockCandles.asStateFlow()

    private var _brandNewData = false
    val brandNewData: Boolean
        get() = _brandNewData

    private val cache: MutableMap<StockCandleParam, StockCandles> = mutableMapOf()

    private fun updateChart(
        newStockCandleParam: StockCandleParam = StockCandleParam.MONTH,
        repeat: Boolean = false
    ) {
        if (_stockCandleParam.value != newStockCandleParam || repeat) {
            val newStockCandleParamTemp = if (repeat) {
                _stockCandleParam.value ?: newStockCandleParam
            } else {
                newStockCandleParam
            }

            _brandNewData = false
            beforeUpdate(newStockCandleParamTemp)
            if (cache.containsKey(newStockCandleParamTemp)) {
                _brandNewData = true
                _stockCandles.value = cache[newStockCandleParamTemp]
                stopLoading()
            } else {
                beforeUpdateStart()
                startJob(stopImmediately = true) {
                    doJob(newStockCandleParamTemp)
                }
            }
        }
    }

    private fun beforeUpdate(newStockCandleParam: StockCandleParam) {
        _previousStockCandleParam = _stockCandleParam.value
        _stockCandleParam.value = newStockCandleParam
        stopJob()
    }

    private fun beforeUpdateStart() {
        startLoading()
        startTimeoutJob()
    }

    private suspend fun doJob(newStockCandleParam: StockCandleParam) {
        val newStockCandles = repository.getStockCandles(ticker, newStockCandleParam)
        if (newStockCandles != null) {
            if (newStockCandles.price.size > 1) {
                jobDone()
            }
            _brandNewData = true
            setNewStockCandles(newStockCandleParam, newStockCandles)
        }
        val newRefreshedStockCandles =
            repository.getStockCandlesWithRefresh(ticker, newStockCandleParam)
        if (newRefreshedStockCandles != null) {
            _brandNewData = (newStockCandles == null)
            setNewStockCandles(newStockCandleParam, newRefreshedStockCandles)
        }
        if (newStockCandles == null && newRefreshedStockCandles == null) {
            setNewStockCandles(newStockCandleParam)
        }
    }

    private fun setNewStockCandles(
        newStockCandleParam: StockCandleParam,
        stockCandlesNew: StockCandles? = null
    ) {
        stopTimeoutJob()
        if (stockCandlesNew != null) {
            cache[newStockCandleParam] = stockCandlesNew
        } else {
            cache.remove(newStockCandleParam)
        }
        _stockCandles.value = stockCandlesNew
        stopLoading()
    }

    override fun onTimeout() {
        Timber.i("onTimeout")
        _stockCandles.value = null
        stopLoading()
    }

    override fun onError(e: Throwable) {
        Timber.w(e, "onError")
        stopTimeoutJob()
        if (stopJob()) {
            _stockCandles.value = null
        }
        stopLoading()
    }

    fun dayChart() = updateChart(StockCandleParam.DAY)

    fun weekChart() = updateChart(StockCandleParam.WEEK)

    fun monthChart() = updateChart(StockCandleParam.MONTH)

    fun sixMonthsChart() = updateChart(StockCandleParam.SIX_MONTHS)

    fun yearChart() = updateChart(StockCandleParam.YEAR)

    fun allTimeChart() = updateChart(StockCandleParam.ALL_TIME)
}
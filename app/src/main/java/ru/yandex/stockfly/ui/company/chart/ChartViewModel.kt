package ru.yandex.stockfly.ui.company.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.yandex.stockfly.base.DownloadableViewModel
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.model.StockCandles
import ru.yandex.stockfly.other.StockCandleParam
import ru.yandex.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(ChartFragment.TICKER_KEY)!!

    private var _stockCandleParam = MutableLiveData<StockCandleParam?>()
    val stockCandleParam: LiveData<StockCandleParam>
        get() = _stockCandleParam.map { it!! }

    private var _previousStockCandleParam: StockCandleParam? = null
    val previousStockCandleParam: StockCandleParam?
        get() = _previousStockCandleParam

    val company: LiveData<Company> = repository.getCompany(ticker).map {
        updateChart()
        it
    }

    private val _stockCandles = MutableLiveData<StockCandles?>()
    val stockCandles: LiveData<StockCandles?> = _stockCandles

    private val cacheMap: MutableMap<StockCandleParam, StockCandles> = mutableMapOf()

    private fun updateChart(newStockCandleParam: StockCandleParam = StockCandleParam.MONTH) {
        if (_stockCandleParam.value != newStockCandleParam) {
            beforeUpdate(newStockCandleParam)
            if (cacheMap.containsKey(newStockCandleParam)) {
                _stockCandles.postValue(cacheMap[newStockCandleParam])
            } else {
                beforeUpdateStart()
                startJob {
                    val stockCandles = repository.getStockCandles(ticker, newStockCandleParam)
                    if (stopTimeoutJob()) {
                        _stockCandles.postValue(stockCandles)
                        cacheMap[newStockCandleParam] = stockCandles
                        stopLoading()
                    }
                }
            }
        }
    }

    private fun beforeUpdate(newStockCandleParam: StockCandleParam) {
        _previousStockCandleParam = _stockCandleParam.value
        _stockCandleParam.postValue(newStockCandleParam)
    }

    private fun beforeUpdateStart() {
        stopJob()
        startLoading()
        startTimeoutJob()
    }

    override fun onTimeout() {
        _stockCandles.postValue(null)
        stopLoading()
    }

    override fun onError(e: Throwable) {
        stopTimeoutJob()
        _stockCandles.postValue(null)
        stopLoading()
    }

    fun dayChart() = updateChart(StockCandleParam.DAY)
    fun weekChart() = updateChart(StockCandleParam.WEEK)
    fun monthChart() = updateChart(StockCandleParam.MONTH)
    fun sixMonthsChart() = updateChart(StockCandleParam.SIX_MONTHS)
    fun yearChart() = updateChart(StockCandleParam.YEAR)
    fun allTimeChart() = updateChart(StockCandleParam.ALL_TIME)
}
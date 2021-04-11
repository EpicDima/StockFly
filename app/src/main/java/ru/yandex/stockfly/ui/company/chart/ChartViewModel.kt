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

    private var _brandNewData = false
    val brandNewData: Boolean
        get() = _brandNewData

    private val cache: MutableMap<StockCandleParam, StockCandles> = mutableMapOf()

    private fun updateChart(newStockCandleParam: StockCandleParam = StockCandleParam.MONTH) {
        if (_stockCandleParam.value != newStockCandleParam) {
            _brandNewData = false
            beforeUpdate(newStockCandleParam)
            if (cache.containsKey(newStockCandleParam)) {
                _brandNewData = true
                _stockCandles.postValue(cache[newStockCandleParam])
                stopLoading()
            } else {
                beforeUpdateStart()
                startJob(stopImmediately = true) {
                    doJob(newStockCandleParam)
                }
            }
        }
    }

    private fun beforeUpdate(newStockCandleParam: StockCandleParam) {
        _previousStockCandleParam = _stockCandleParam.value
        _stockCandleParam.postValue(newStockCandleParam)
        stopJob()
    }

    private fun beforeUpdateStart() {
        startLoading()
        startTimeoutJob()
    }

    private suspend fun doJob(newStockCandleParam: StockCandleParam) {
        val newStockCandles = repository.getStockCandles(ticker, newStockCandleParam)
        if (newStockCandles != null) {
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
        _stockCandles.postValue(stockCandlesNew)
        stopLoading()
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
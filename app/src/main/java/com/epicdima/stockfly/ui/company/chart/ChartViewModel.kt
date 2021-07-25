package com.epicdima.stockfly.ui.company.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.model.StockCandles
import com.epicdima.stockfly.other.StockCandleParam
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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
        _stockCandles.postValue(stockCandlesNew)
        stopLoading()
    }

    override fun onTimeout() {
        Timber.i("onTimeout")
        _stockCandles.postValue(null)
        stopLoading()
    }

    override fun onError(e: Throwable) {
        Timber.w(e, "onError")
        stopTimeoutJob()
        if (stopJob()) {
            _stockCandles.postValue(null)
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
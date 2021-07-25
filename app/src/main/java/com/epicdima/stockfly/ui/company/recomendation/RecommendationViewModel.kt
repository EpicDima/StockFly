package com.epicdima.stockfly.ui.company.recomendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Recommendation
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_RANGE = 12

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(RecommendationFragment.TICKER_KEY)!!

    private var _length = 0
    val length: Int
        get() = _length

    private var _beginIndex = -1
    var beginIndex: Int
        get() = _beginIndex
        set(value) {
            if (_beginIndex != value) {
                _beginIndex = value
                updatePeriodRange()
            }
        }

    private var _endIndex = -1
    var endIndex: Int
        get() = _endIndex
        set(value) {
            if (_endIndex != value) {
                _endIndex = value
                updatePeriodRange()
            }
        }

    private var _brandNewData = true
    val brandNewData: Boolean
        get() = _brandNewData

    private val _periodRange = MutableLiveData<Pair<String, String>>()
    val periodRange: LiveData<Pair<String, String>> = _periodRange

    private val _recommendations = mutableListOf<Recommendation>()
    val recommendations: List<Recommendation>
        get() = if (_recommendations.isNotEmpty()) _recommendations.subList(
            beginIndex,
            minOf(endIndex + 1, _recommendations.size)
        ) else emptyList()

    init {
        Timber.v("init with ticker '%s'", ticker)

        startLoading()
        startTimeoutJob()
        startJob(Dispatchers.Main) {
            repository.getCompanyRecommendationsWithRefresh(ticker, viewModelScope).observeForever {
                if (stopTimeoutJob()) {
                    Timber.i("loaded recommendations %s", it)
                    _recommendations.addAll(it)
                    _length = it.size
                    _beginIndex = maxOf(0, it.lastIndex - DEFAULT_RANGE)
                    _endIndex = it.lastIndex
                    updatePeriodRange()
                    _brandNewData = false
                    stopLoading()
                }
            }
        }
    }

    private fun updatePeriodRange() {
        if (_recommendations.isNotEmpty()) {
            _brandNewData = true
            _periodRange.postValue(
                Pair(
                    _recommendations[beginIndex].periodFormatted,
                    _recommendations[endIndex].periodFormatted
                )
            )
        }
    }

    override fun onTimeout() {
        Timber.i("onTimeout")
        setError()
        stopLoading()
    }

    override fun onError(e: Throwable) {
        Timber.w(e, "onError")
        stopTimeoutJob()
        setError()
        stopLoading()
    }
}
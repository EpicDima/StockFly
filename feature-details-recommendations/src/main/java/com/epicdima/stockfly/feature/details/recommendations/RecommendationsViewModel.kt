package com.epicdima.stockfly.feature.details.recommendations

import androidx.lifecycle.SavedStateHandle
import com.epicdima.stockfly.core.common.DownloadableViewModel
import com.epicdima.stockfly.core.data.Repository
import com.epicdima.stockfly.core.model.Recommendation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_RANGE = 12

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(RecommendationsFragment.TICKER_KEY)!!

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

    private val _periodRange = MutableStateFlow<Pair<Recommendation, Recommendation>?>(null)
    val periodRange: StateFlow<Pair<Recommendation, Recommendation>?> = _periodRange.asStateFlow()

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
            repository.getCompanyRecommendationsWithRefresh(ticker)
                .collect {
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
            _periodRange.value = Pair(
                _recommendations[beginIndex],
                _recommendations[endIndex]
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
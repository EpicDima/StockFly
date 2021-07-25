package com.epicdima.stockfly.ui.company.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.NewsItem
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(NewsFragment.TICKER_KEY)!!

    private val _news = MutableLiveData<List<NewsItem>>()
    val news: LiveData<List<NewsItem>> = _news

    init {
        Timber.v("init with ticker '%s'", ticker)

        startLoading()
        startTimeoutJob()
        startJob(Dispatchers.Main) {
            repository.getCompanyNewsWithRefresh(ticker, viewModelScope).observeForever {
                Timber.i("loaded news %s", it)
                if (stopTimeoutJob()) {
                    _news.postValue(it)
                    stopLoading()
                }
            }
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
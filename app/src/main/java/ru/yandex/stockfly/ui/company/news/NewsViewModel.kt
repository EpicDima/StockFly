package ru.yandex.stockfly.ui.company.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.yandex.stockfly.base.DownloadableViewModel
import ru.yandex.stockfly.model.NewsItem
import ru.yandex.stockfly.repository.Repository
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
        startLoading()
        startJob(Dispatchers.Main) {
            startTimeoutJob()
            repository.getCompanyNewsWithRefresh(ticker, viewModelScope).observeForever {
                if (stopTimeoutJob()) {
                    _news.postValue(it)
                    stopLoading()
                }
            }
        }
    }

    override fun onTimeout() {
        setError()
        stopLoading()
    }

    override fun onError(e: Throwable) {
        setError()
        stopLoading()
    }
}
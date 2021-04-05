package ru.yandex.stockfly.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.yandex.stockfly.base.DownloadableViewModel
import ru.yandex.stockfly.model.SearchItem
import ru.yandex.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
) : DownloadableViewModel() {

    private val _showPopular = MutableLiveData(false)
    val showPopular: LiveData<Boolean> = _showPopular

    private val _showSearched = MutableLiveData(false)
    val showSearched: LiveData<Boolean> = _showSearched

    private val _showResult = MutableLiveData(false)
    val showResult: LiveData<Boolean> = _showResult

    private val _emptyResult = MutableLiveData(false)
    val emptyResult: LiveData<Boolean> = _emptyResult

    private val _popular = MutableLiveData(POPULAR)
    val popular: LiveData<List<String>> = _popular

    private val _searched = MutableLiveData(repository.searchedRequests.toList())
    val searched: LiveData<List<String>> = _searched

    private val _result = MutableLiveData<List<SearchItem>>()
    val result: LiveData<List<SearchItem>> = _result

    init {
        reset()
    }

    fun reset() {
        stopJob()
        resetError()
        stopLoading()
        _showPopular.value = _popular.value!!.isNotEmpty()
        _showSearched.value = _searched.value!!.isNotEmpty()
        _showResult.value = false
        _emptyResult.value = false
        _searched.postValue(repository.searchedRequests.toList())
        _result.value = emptyList()
    }

    fun search(query: String): Boolean {
        val text = query.trim()
        return if (text.isNotEmpty()) {
            beforeSearchStart()
            startJob(stopImmediately = true) {
                repository.addSearchRequest(text)
                val list = repository.search(text)
                if (stopTimeoutJob()) {
                    onLoad(list)
                    stopLoading()
                }
            }
            true
        } else {
            false
        }
    }

    private fun beforeSearchStart() {
        startLoading()
        _showResult.value = false
        _emptyResult.value = false
        _showPopular.value = false
        _showSearched.value = false
        stopJob()
        startTimeoutJob()
    }

    private fun onLoad(list: List<SearchItem>) {
        _result.postValue(list)
        if (list.isNotEmpty()) {
            _showResult.postValue(true)
        } else {
            _emptyResult.postValue(true)
        }
    }

    override fun onTimeout() {
        setError()
        stopLoading()
    }

    override fun onError(e: Throwable) {
        stopTimeoutJob()
        stopLoading()
        setError()
    }
}


private val POPULAR = listOf(
    "Yandex",
    "Tesla",
    "Google",
    "Apple",
    "Nvidia",
    "AMD",
    "Microsoft",
    "GM",
    "Alibaba",
    "Facebook",
    "Intel",
    "Visa",
)
package com.epicdima.stockfly.ui.search

import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.BuildConfig
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository,
) : DownloadableViewModel() {

    private val _showPopular = MutableStateFlow(false)
    val showPopular: StateFlow<Boolean> = _showPopular.asStateFlow()

    private val _showSearched = MutableStateFlow(false)
    val showSearched: StateFlow<Boolean> = _showSearched.asStateFlow()

    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> = _showResult.asStateFlow()

    private val _emptyResult = MutableStateFlow(false)
    val emptyResult: StateFlow<Boolean> = _emptyResult.asStateFlow()

    private val _popular = MutableStateFlow(POPULAR)
    val popular: StateFlow<List<String>> = _popular.asStateFlow()

    private val _searched = MutableStateFlow(repository.searchedRequests.toList())
    val searched: StateFlow<List<String>> = _searched.asStateFlow()

    private val _result = MutableStateFlow<List<Company>>(emptyList())
    val result: StateFlow<List<Company>> = _result.asStateFlow()

    init {
        Timber.v("init")
        reset()
    }

    fun reset() {
        stopJob()
        stopTimeoutJob()
        resetError()
        stopLoading()
        _showPopular.value = _popular.value.isNotEmpty()
        _showSearched.value = _searched.value.isNotEmpty()
        _showResult.value = false
        _emptyResult.value = false
        _searched.value = repository.searchedRequests.toList()
        _result.value = emptyList()
    }

    fun search(query: String): Boolean {
        Timber.i("search '%s'", query)

        val text = query.trim()
        return if (text.isNotEmpty()) {
            beforeSearchStart()
            startJob(stopImmediately = true) {
                repository.addSearchRequest(text)
                val list = repository.search(text)
                if (stopTimeoutJob()) {
                    onLoad(list)
                    stopLoading()
                    if (BuildConfig.DETAILED_SEARCH) {
                        getMoreInformation(list)
                    }
                }
            }
            true
        } else {
            false
        }
    }

    private fun beforeSearchStart() {
        resetError()
        stopJob()
        startLoading()
        _showResult.value = false
        _emptyResult.value = false
        _showPopular.value = false
        _showSearched.value = false
        startTimeoutJob()
    }

    private fun onLoad(list: List<Company>) {
        Timber.i("onLoad companies = %s", list)

        _result.value = list
        if (list.isNotEmpty()) {
            _showResult.value = true
            _emptyResult.value = false
        } else {
            _showResult.value = false
            _emptyResult.value = true
        }
    }

    private suspend fun getMoreInformation(list: List<Company>) {
        val mutableList = list.toMutableList()
        list.forEachIndexed { index, company ->
            viewModelScope.launch(Dispatchers.IO) {
                mutableList[index] = repository.getCompanyForSearch(company)
                _result.value = mutableList.toList()
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
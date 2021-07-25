package com.epicdima.stockfly.ui.company.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(SummaryFragment.TICKER_KEY)!!

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    init {
        startJob(Dispatchers.Main) {
            startTimeoutJob()
            repository.getCompany(ticker, viewModelScope).observeForever {
                if (stopTimeoutJob()) {
                    _company.postValue(it)
                    stopLoading()
                }
            }
        }
    }

    override fun onTimeout() {
        setError()
    }

    override fun onError(e: Throwable) {
        stopTimeoutJob()
        setError()
    }
}
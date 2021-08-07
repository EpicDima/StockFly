package com.epicdima.stockfly.ui.company

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(CompanyFragment.TICKER_KEY)!!

    private val _company = MutableStateFlow<Company?>(null)
    val company: StateFlow<Company?> = _company.asStateFlow()

    init {
        Timber.v("init with ticker '%s'", ticker)

        startTimeoutJob()
        startJob(Dispatchers.Main) {
            repository.getCompanyWithRefresh(ticker)
                .collect {
                    if (stopTimeoutJob()) {
                        _company.value = it
                        stopLoading()
                    }
                }
        }
        viewModelScope.launch {
            repository.getCompany(ticker).collect {
                _company.value = it
                Timber.i("loaded company %s", it)
            }
        }
    }

    fun changeFavourite() = viewModelScope.launch(Dispatchers.IO) {
        Timber.i("changeFavourite %s", company)
        repository.changeFavourite(company.value!!)
    }

    override fun onTimeout() {
        Timber.i("onTimeout")
        setError()
    }

    override fun onError(e: Throwable) {
        Timber.w(e, "onError")
        stopTimeoutJob()
        setError()
    }
}
package com.epicdima.stockfly.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.base.DownloadableViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import com.epicdima.stockfly.shortcut.ShortcutConfigurator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(CompanyFragment.TICKER_KEY)!!

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    init {
        Timber.v("init with ticker '%s'", ticker)

        startTimeoutJob()
        startJob(Dispatchers.Main) {
            repository.getCompanyWithRefresh(ticker, viewModelScope).observeForever {
                if (stopTimeoutJob()) {
                    _company.postValue(it)
                    Timber.i("loaded company %s", it)
                }
            }
        }

        repository.favourites.observeForever {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(it)
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
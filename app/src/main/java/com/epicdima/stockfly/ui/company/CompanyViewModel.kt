package com.epicdima.stockfly.ui.company

import androidx.lifecycle.*
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

    private lateinit var companyLiveData: LiveData<Company>
    private lateinit var companyObserver: Observer<Company>
    private val favouritesLiveData: LiveData<List<Company>>
    private val favouritesObserver: Observer<List<Company>>

    init {
        Timber.v("init with ticker '%s'", ticker)

        startTimeoutJob()
        startJob(Dispatchers.Main) {
            companyObserver = Observer {
                if (stopTimeoutJob()) {
                    _company.postValue(it)
                    Timber.i("loaded company %s", it)
                }
            }
            companyLiveData = repository.getCompanyWithRefresh(ticker, viewModelScope).apply {
                observeForever(companyObserver)
            }
        }

        favouritesObserver = Observer {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(it)
            }
        }
        favouritesLiveData = repository.favourites.apply {
            observeForever(favouritesObserver)
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

    override fun onCleared() {
        companyLiveData.removeObserver(companyObserver)
        favouritesLiveData.removeObserver(favouritesObserver)
        super.onCleared()
    }
}
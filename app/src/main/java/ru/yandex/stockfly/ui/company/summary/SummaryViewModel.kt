package ru.yandex.stockfly.ui.company.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.yandex.stockfly.base.DownloadableViewModel
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
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
                }
            }
        }
    }

    override fun onTimeout() {
        setError()
    }

    override fun onError(e: Throwable) {
        setError()
    }
}
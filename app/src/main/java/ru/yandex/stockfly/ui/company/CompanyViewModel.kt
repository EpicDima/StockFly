package ru.yandex.stockfly.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yandex.stockfly.base.DownloadableViewModel
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class CompanyViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : DownloadableViewModel() {

    private val ticker = state.get<String>(CompanyFragment.TICKER_KEY)!!

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    init {
        startTimeoutJob()
        startJob(Dispatchers.Main) {
            repository.getCompanyWithRefresh(ticker, viewModelScope).observeForever {
                if (stopTimeoutJob()) {
                    _company.postValue(it)
                }
            }
        }
    }

    fun changeFavourite() = viewModelScope.launch(Dispatchers.IO) {
        repository.changeFavourite(company.value!!)
        _company.postValue(company.value!!.copy(favourite = !company.value!!.favourite))
    }

    override fun onTimeout() {
        setError()
    }

    override fun onError(e: Throwable) {
        stopTimeoutJob()
        setError()
    }
}
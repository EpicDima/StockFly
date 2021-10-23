package com.epicdima.stockfly.ui.company.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : ViewModel() {

    private val ticker = state.get<String>(SummaryFragment.TICKER_KEY)!!

    private val _company = MutableStateFlow<Company?>(null)
    val company: StateFlow<Company?> = _company.asStateFlow()

    init {
        Timber.v("init with ticker '%s'", ticker)

        viewModelScope.launch(Dispatchers.Main) {
            repository.getCompany(ticker).collect {
                Timber.i("loaded company %s", it)
                _company.value = it
            }
        }
    }
}
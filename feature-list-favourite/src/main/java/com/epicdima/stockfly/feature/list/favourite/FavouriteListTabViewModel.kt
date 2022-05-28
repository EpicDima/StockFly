package com.epicdima.stockfly.feature.list.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.core.data.Repository
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.core.navigation.OpenDetailsProvider
import com.epicdima.stockfly.core.shortcuts.ShortcutsConfigurator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteListTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutsConfigurator,
    private val openDetailsProvider: OpenDetailsProvider,
) : ViewModel() {

    val companies: StateFlow<List<Company>> = repository.favourites
        .distinctUntilChanged()
        .onEach { shortcutConfigurator.updateShortcuts(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun changeFavouriteNumber(from: Int, to: Int) {
        Timber.i("changeFavouriteNumber from %d to %d", from, to)

        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }

    fun changeFavourite(company: Company) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavourite(company)
        }
    }

    fun deleteCompany(company: Company) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompany(company)
        }
    }

    fun openCompanyDetails(ticker: String) {
        openDetailsProvider.openDetails(ticker)
    }
}
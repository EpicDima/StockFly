package com.epicdima.stockfly.ui.main.favourite

import androidx.lifecycle.*
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import com.epicdima.stockfly.shortcut.ShortcutConfigurator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
) : ViewModel() {

    val companies: LiveData<List<Company>> =
        repository.favourites.asLiveData(viewModelScope.coroutineContext)

    private val favouritesLiveData: LiveData<List<Company>>
    private val favouritesObserver: Observer<List<Company>>

    init {
        Timber.v("init")
        favouritesObserver = Observer {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(it)
            }
        }
        favouritesLiveData =
            repository.favourites.asLiveData(viewModelScope.coroutineContext).apply {
                observeForever(favouritesObserver)
            }
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        Timber.i("changeFavouriteNumber from %d to %d", from, to)

        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }

    override fun onCleared() {
        favouritesLiveData.removeObserver(favouritesObserver)
        super.onCleared()
    }
}
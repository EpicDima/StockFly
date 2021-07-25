package com.epicdima.stockfly.ui.main.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import com.epicdima.stockfly.shortcut.ShortcutConfigurator
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
) : ViewModel() {

    val companies: LiveData<List<Company>> = repository.favourites

    init {
        Timber.v("init")
        companies.observeForever {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(it)
            }
        }
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        Timber.i("changeFavouriteNumber from %d to %d", from, to)

        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }
}
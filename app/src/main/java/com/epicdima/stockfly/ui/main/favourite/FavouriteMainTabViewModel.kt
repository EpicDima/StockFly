package com.epicdima.stockfly.ui.main.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import com.epicdima.stockfly.shortcut.ShortcutConfigurator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
) : ViewModel() {

    val companies: StateFlow<List<Company>> = repository.favourites
        .onEach {
            viewModelScope.launch(Dispatchers.Default) {
                shortcutConfigurator.updateShortcuts(it)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun changeFavouriteNumber(from: Int, to: Int) {
        Timber.i("changeFavouriteNumber from %d to %d", from, to)

        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }
}
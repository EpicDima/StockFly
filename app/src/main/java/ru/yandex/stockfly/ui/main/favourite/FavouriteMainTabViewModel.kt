package ru.yandex.stockfly.ui.main.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
import ru.yandex.stockfly.shortcut.ShortcutConfigurator
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
) : ViewModel() {

    val companies: LiveData<List<Company>> = repository.favourites

    init {
        companies.observeForever {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(it)
            }
        }
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }
}
package ru.yandex.stockfly.ui.main.favourite

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
import ru.yandex.stockfly.shortcut.ShortcutConfigurator
import ru.yandex.stockfly.ui.MainActivity
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    private val shortcutConfigurator: ShortcutConfigurator,
    application: Application
) : AndroidViewModel(application) {

    val companies: LiveData<List<Company>> = repository.favourites

    init {
        companies.observeForever {
            viewModelScope.launch {
                shortcutConfigurator.updateShortcuts(getApplication(), it)
            }
        }
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }
}
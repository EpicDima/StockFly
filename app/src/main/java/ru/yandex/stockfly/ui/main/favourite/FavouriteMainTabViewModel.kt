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
import ru.yandex.stockfly.ui.MainActivity
import javax.inject.Inject

@HiltViewModel
class FavouriteMainTabViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    val companies: LiveData<List<Company>> = repository.favourites

    init {
        companies.observeForever {
            viewModelScope.launch {
                updateShortcuts(it)
            }
        }
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavouriteNumber(from, to)
        }
    }

    private suspend fun updateShortcuts(list: List<Company>) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context())
        val maxFavouriteShortcutsNumber = ShortcutManagerCompat
            .getMaxShortcutCountPerActivity(context()) - 1
        ShortcutManagerCompat.addDynamicShortcuts(
            context(),
            list.take(maxFavouriteShortcutsNumber - 1).map { createShortcut(it) }.toList()
        )
    }

    private suspend fun createShortcut(company: Company): ShortcutInfoCompat {
        val intent = Intent(context(), MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(
                MainActivity.FRAGMENT_KEY,
                MainActivity.COMPANY_FRAGMENT_VALUE_PREFIX + company.ticker
            )
        }
        val imageRequest = ImageRequest.Builder(context()).data(company.logoUrl).build()
        val bitmap = context().imageLoader
            .execute(imageRequest).drawable?.toBitmap()
        var shortcutInfoBuilder = ShortcutInfoCompat.Builder(context(), company.ticker)
            .setShortLabel(company.ticker)
            .setLongLabel(company.name)
            .setIntent(intent)
        if (bitmap != null) {
            shortcutInfoBuilder = shortcutInfoBuilder.setIcon(IconCompat.createWithBitmap(bitmap))
        }
        return shortcutInfoBuilder.build()
    }

    private fun context(): Context {
        return getApplication()
    }
}
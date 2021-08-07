package com.epicdima.stockfly.shortcut

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.epicdima.stockfly.R
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.other.executeImageRequest
import com.epicdima.stockfly.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.EmptyCoroutineContext

class ShortcutConfigurator(
    private val context: Context
) {
    private val companiesShortcutsQueueMutableFlow = MutableStateFlow<List<Company>>(emptyList())

    init {
        CoroutineScope(EmptyCoroutineContext).launch {
            companiesShortcutsQueueMutableFlow
                .debounce(2500)
                .map { list -> list.take(maxFavouriteShortcutsNumber - 1) }
                .distinctUntilChanged()
                .map { list -> list.map { createShortcut(it) } }
                .flowOn(Dispatchers.Default)
                .collect { list ->
                    Timber.v("real updateShortcuts")
                    ShortcutManagerCompat.removeAllDynamicShortcuts(context)
                    ShortcutManagerCompat.addDynamicShortcuts(context, list)
                }
        }
    }

    private val maxFavouriteShortcutsNumber = ShortcutManagerCompat
        .getMaxShortcutCountPerActivity(context) - 1

    private val defaultCompanyShortcutIntent = Intent(context, MainActivity::class.java)
        .setAction(Intent.ACTION_VIEW)

    private val defaultCompanyShortcutIcon = IconCompat.createWithResource(
        context,
        R.mipmap.ic_launcher_round
    )

    fun updateShortcuts(list: List<Company>) {
        Timber.i("updateShortcuts")
        companiesShortcutsQueueMutableFlow.value = list
    }

    private suspend fun createShortcut(company: Company): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, company.ticker)
            .setShortLabel(company.ticker)
            .setLongLabel(company.name)
            .setIntent(createCompanyIntent(company))
            .setIcon(createCompanyIcon(company))
            .build()
    }

    private fun createCompanyIntent(company: Company): Intent {
        return Intent(defaultCompanyShortcutIntent)
            .setData(Uri.parse(company.ticker))
    }

    private suspend fun createCompanyIcon(company: Company): IconCompat {
        return context.executeImageRequest(company.logoUrl)
            .drawable
            ?.toBitmap()
            ?.let { IconCompat.createWithBitmap(it) }
            ?: defaultCompanyShortcutIcon
    }
}
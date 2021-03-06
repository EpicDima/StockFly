package com.epicdima.stockfly.shortcuts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.epicdima.stockfly.R
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.core.shortcuts.ShortcutsConfigurator
import com.epicdima.stockfly.core.utils.executeImageRequest
import com.epicdima.stockfly.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber

@OptIn(FlowPreview::class)
class RealShortcutsConfigurator(
    private val context: Context
) : ShortcutsConfigurator {

    private val companiesShortcutsQueueMutableFlow = MutableStateFlow<List<Company>>(emptyList())

    init {
        companiesShortcutsQueueMutableFlow
            .debounce(2500)
            .map { list -> list.take(maxFavouriteShortcutsNumber - 1) }
            .distinctUntilChanged()
            .map { list -> list.map { createShortcut(it) } }
            .onEach { list ->
                Timber.v("real updateShortcuts")
                ShortcutManagerCompat.removeAllDynamicShortcuts(context)
                ShortcutManagerCompat.addDynamicShortcuts(context, list)
            }
            .launchIn(CoroutineScope(Dispatchers.Default))
    }

    private val maxFavouriteShortcutsNumber = ShortcutManagerCompat
        .getMaxShortcutCountPerActivity(context) - 1

    private val defaultCompanyShortcutIntent = Intent(context, MainActivity::class.java)
        .setAction(Intent.ACTION_VIEW)

    private val defaultCompanyShortcutIcon = IconCompat.createWithResource(
        context,
        R.mipmap.ic_launcher_round
    )

    override fun updateShortcuts(list: List<Company>) {
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
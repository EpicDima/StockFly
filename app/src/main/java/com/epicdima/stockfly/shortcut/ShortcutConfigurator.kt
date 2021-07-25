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
import timber.log.Timber

class ShortcutConfigurator(
    private val context: Context
) {
    private val maxFavouriteShortcutsNumber = ShortcutManagerCompat
        .getMaxShortcutCountPerActivity(context) - 1

    private val defaultCompanyShortcutIntent = Intent(context, MainActivity::class.java)
        .setAction(Intent.ACTION_VIEW)

    suspend fun updateShortcuts(list: List<Company>) {
        Timber.i("updateShortcuts %s", list)

        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        ShortcutManagerCompat.addDynamicShortcuts(
            context,
            list.take(maxFavouriteShortcutsNumber - 1)
                .map { createShortcut(it) }
        )
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
        val bitmap = context.executeImageRequest(company.logoUrl)
            .drawable
            ?.toBitmap()

        return if (bitmap != null) {
            IconCompat.createWithBitmap(bitmap)
        } else {
            IconCompat.createWithResource(
                context,
                R.mipmap.ic_launcher_round
            )
        }
    }
}
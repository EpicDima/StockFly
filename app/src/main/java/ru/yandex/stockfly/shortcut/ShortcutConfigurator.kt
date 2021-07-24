package ru.yandex.stockfly.shortcut

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import ru.yandex.stockfly.R
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.ui.MainActivity

class ShortcutConfigurator(
    private val context: Context
) {
    private val maxFavouriteShortcutsNumber = ShortcutManagerCompat
        .getMaxShortcutCountPerActivity(context) - 1

    private val defaultCompanyShortcutIntent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
    }

    suspend fun updateShortcuts(list: List<Company>) {
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
        val bitmap = context.imageLoader
            .execute(
                ImageRequest.Builder(context)
                    .memoryCacheKey(company.logoUrl)
                    .data(company.logoUrl)
                    .build()
            )
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
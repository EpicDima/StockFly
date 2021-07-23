package ru.yandex.stockfly.shortcut

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.ui.MainActivity

class ShortcutConfigurator {

    suspend fun updateShortcuts(context: Context, list: List<Company>) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
        val maxFavouriteShortcutsNumber = ShortcutManagerCompat
            .getMaxShortcutCountPerActivity(context) - 1
        ShortcutManagerCompat.addDynamicShortcuts(
            context,
            list.take(maxFavouriteShortcutsNumber - 1).map { createShortcut(context, it) }.toList()
        )
    }

    private suspend fun createShortcut(context: Context, company: Company): ShortcutInfoCompat {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(
                MainActivity.FRAGMENT_KEY,
                MainActivity.COMPANY_FRAGMENT_VALUE_PREFIX + company.ticker
            )
        }
        val imageRequest = ImageRequest.Builder(context).data(company.logoUrl).build()
        val bitmap = context.imageLoader
            .execute(imageRequest).drawable?.toBitmap()
        var shortcutInfoBuilder = ShortcutInfoCompat.Builder(context, company.ticker)
            .setShortLabel(company.ticker)
            .setLongLabel(company.name)
            .setIntent(intent)
        if (bitmap != null) {
            shortcutInfoBuilder = shortcutInfoBuilder.setIcon(IconCompat.createWithBitmap(bitmap))
        }
        return shortcutInfoBuilder.build()
    }
}
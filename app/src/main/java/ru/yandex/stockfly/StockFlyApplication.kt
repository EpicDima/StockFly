package ru.yandex.stockfly

import android.app.Application
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import ru.yandex.stockfly.other.Formatter
import ru.yandex.stockfly.other.Refresher
import ru.yandex.stockfly.other.locale
import javax.inject.Inject

@HiltAndroidApp
class StockFlyApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var refresher: Refresher

    @Inject
    lateinit var formatter: Formatter

    override fun onCreate() {
        super.onCreate()
        refresher.refresh()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        formatter.updateLocale(newConfig.locale())
        super.onConfigurationChanged(newConfig)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}
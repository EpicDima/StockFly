package com.epicdima.stockfly

import android.app.Application
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import dagger.hilt.android.HiltAndroidApp
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.Refresher
import com.epicdima.stockfly.other.locale
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
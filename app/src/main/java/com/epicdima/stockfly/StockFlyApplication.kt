package com.epicdima.stockfly

import android.app.Application
import android.content.res.Configuration
import android.util.Log.VERBOSE
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.Refresher
import com.epicdima.stockfly.other.locale
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StockFlyApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var refresher: Refresher

    @Inject
    lateinit var formatter: Formatter

    override fun onCreate() {
        super.onCreate()

        initTimber()
        refresher.refresh()

        Timber.i("onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.i("onConfigurationChanged %s", newConfig)
        formatter.updateLocale(newConfig.locale())
        super.onConfigurationChanged(newConfig)
    }

    override fun newImageLoader(): ImageLoader {
        val builder = ImageLoader.Builder(applicationContext)

        if (BuildConfig.DEBUG) {
            builder.logger(DebugLogger(VERBOSE))
        }

        return builder.build()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
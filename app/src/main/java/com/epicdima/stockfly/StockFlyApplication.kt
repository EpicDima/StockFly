package com.epicdima.stockfly

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log.VERBOSE
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.Refresher
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StockFlyApplication : Application(), ImageLoaderFactory {

    private lateinit var configuration: Configuration

    @Inject
    lateinit var formatter: Formatter

    @Inject
    lateinit var refresher: Refresher

    override fun onCreate() {
        super.onCreate()
        configuration = Configuration(resources.configuration)

        initTimber()
        refresher.refresh()

        Timber.i("onCreate")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (configuration.diff(newConfig).and(ActivityInfo.CONFIG_LOCALE) != 0) {
            formatter.refresh(this)
        }

        configuration = newConfig
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
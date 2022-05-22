package com.epicdima.stockfly

import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.StrictMode
import android.util.Log.VERBOSE
import androidx.hilt.work.HiltWorkerFactory
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.epicdima.stockfly.core.work.RefreshWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StockFlyApplication : Application(), ImageLoaderFactory,
    androidx.work.Configuration.Provider {

    private lateinit var configuration: Configuration

    @Inject
    lateinit var formatter: com.epicdima.stockfly.core.formatter.Formatter

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        Timber.i("onCreate Start")
        super.onCreate()

        configuration = Configuration(resources.configuration)

        initTimber()
        initStrictMode()

        RefreshWorker.startRefreshWork(this)

        Timber.i("onCreate Finish")
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

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }

    override fun getWorkManagerConfiguration(): androidx.work.Configuration {
        return androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
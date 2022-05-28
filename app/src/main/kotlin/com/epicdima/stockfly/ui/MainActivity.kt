package com.epicdima.stockfly.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epicdima.stockfly.core.customtabs.CustomTabsProvider
import com.epicdima.stockfly.navigation.StockFlyNavigator
import com.epicdima.stockfly.navigation.StockFlyRouter
import com.github.terrakok.cicerone.NavigatorHolder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigationHolder: NavigatorHolder

    @Inject
    lateinit var router: StockFlyRouter

    @Inject
    lateinit var customTabsProvider: CustomTabsProvider

    private val navigator = StockFlyNavigator(this, android.R.id.content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate")

        lifecycle.addObserver(customTabsProvider)

        if (savedInstanceState == null) {
            router.openList()
            openFragmentByIntent(intent)
        }
    }

    override fun onResumeFragments() {
        Timber.v("onResumeFragments")
        super.onResumeFragments()
        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        Timber.v("onPause")
        navigationHolder.removeNavigator()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        openFragmentByIntent(intent)
    }

    private fun openFragmentByIntent(intent: Intent?) {
        Timber.i(intent.toString())

        when (intent?.action) {
            Intent.ACTION_SEARCH -> router.openSearch()
            Intent.ACTION_VIEW -> intent.dataString?.let {
                router.openDetails(it)
            }
        }
    }

    override fun onBackPressed() {
        router.exit()
    }
}
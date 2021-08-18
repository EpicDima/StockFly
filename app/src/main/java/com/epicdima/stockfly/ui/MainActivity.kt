package com.epicdima.stockfly.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.epicdima.stockfly.databinding.ActivityMainBinding
import com.epicdima.stockfly.other.CustomTabsProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    MainRouter.SearchFragmentOpener,
    MainRouter.CompanyFragmentOpener,
    MainRouter.WebViewFragmentOpener {

    @Inject
    lateinit var customTabsProvider: CustomTabsProvider

    private var router: MainRouter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate")

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.addObserver(customTabsProvider)

        router = MainRouter(supportFragmentManager, binding.fragmentContainer.id)

        if (savedInstanceState == null) {
            openFragmentByIntent(intent)
        }
    }

    override fun onDestroy() {
        Timber.v("onDestroy")
        router = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        openFragmentByIntent(intent)
    }

    private fun openFragmentByIntent(intent: Intent?) {
        Timber.i(intent.toString())

        when (intent?.action) {
            Intent.ACTION_SEARCH -> openSearchFragment()
            Intent.ACTION_VIEW -> intent.dataString?.let { openCompanyFragment(it) }
        }
    }

    override fun onBackPressed() {
        if (router?.back() == false) {
            super.onBackPressed()
        }
    }

    override fun openSearchFragment() {
        router?.openSearchFragment()
    }

    override fun openCompanyFragment(ticker: String) {
        router?.openCompanyFragment(ticker)
    }

    override fun openWebViewFragment(url: String) {
        router?.openWebViewFragment(url)
    }
}
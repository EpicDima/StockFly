package com.epicdima.stockfly.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.epicdima.stockfly.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    MainRouter.SearchFragmentOpener,
    MainRouter.CompanyFragmentOpener,
    MainRouter.WebViewFragmentOpener {

    private var router: MainRouter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        router = MainRouter(supportFragmentManager, binding.fragmentContainer.id)

        if (savedInstanceState == null) {
            openFragmentByIntent(intent)
        }
    }

    override fun onDestroy() {
        router = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        openFragmentByIntent(intent)
    }

    private fun openFragmentByIntent(intent: Intent?) {
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
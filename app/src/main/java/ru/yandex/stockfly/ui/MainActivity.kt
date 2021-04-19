package ru.yandex.stockfly.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SearchFragmentOpener, CompanyFragmentOpener,
    WebViewFragmentOpener {

    companion object {
        private const val FRAGMENT_KEY = "open_fragment"

        const val SEARCH_FRAGMENT_VALUE = "open_search_fragment"
        const val COMPANY_FRAGMENT_VALUE_PREFIX = "open_company_"
    }

    private lateinit var router: MainRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        router = MainRouter(supportFragmentManager, binding.fragmentContainer.id)
        if (savedInstanceState == null) {
            openFragmentByIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        openFragmentByIntent(intent)
    }

    private fun openFragmentByIntent(intent: Intent?) {
        intent?.getStringExtra(FRAGMENT_KEY)?.let {
            if (SEARCH_FRAGMENT_VALUE == it) {
                openSearchFragment()
            } else if (it.startsWith(COMPANY_FRAGMENT_VALUE_PREFIX)) {
                openCompanyFragment(it.removePrefix(COMPANY_FRAGMENT_VALUE_PREFIX))
            }
        }
    }

    override fun onBackPressed() {
        if (!router.back()) {
            super.onBackPressed()
        }
    }

    override fun openSearchFragment() {
        router.openSearchFragment()
    }

    override fun openCompanyFragment(ticker: String) {
        router.openCompanyFragment(ticker)
    }

    override fun openWebViewFragment(url: String) {
        router.openWebViewFragment(url)
    }
}


interface SearchFragmentOpener {
    fun openSearchFragment()
}


interface CompanyFragmentOpener {
    fun openCompanyFragment(ticker: String)
}


interface WebViewFragmentOpener {
    fun openWebViewFragment(url: String)
}
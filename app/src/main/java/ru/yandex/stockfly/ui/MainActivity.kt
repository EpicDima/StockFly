package ru.yandex.stockfly.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SearchFragmentOpener, CompanyFragmentOpener,
    WebViewFragmentOpener {

    private lateinit var router: MainRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        router = MainRouter(supportFragmentManager, binding.fragmentContainer.id)
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
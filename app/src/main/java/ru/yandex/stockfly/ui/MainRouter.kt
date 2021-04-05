package ru.yandex.stockfly.ui

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import ru.yandex.stockfly.ui.company.CompanyFragment
import ru.yandex.stockfly.ui.main.MainFragment
import ru.yandex.stockfly.ui.search.SearchFragment
import ru.yandex.stockfly.ui.web.WebViewFragment

private const val FRAGMENT_MAIN = "main"
private const val FRAGMENT_SEARCH = "search"
private const val FRAGMENT_COMPANY = "company"
private const val FRAGMENT_WEBVIEW = "webview"

class MainRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    init {
        if (fragmentManager.findFragmentByTag(FRAGMENT_MAIN) == null) {
            val mainFragment = MainFragment.newInstance()
            fragmentManager.commitNow {
                add(containerId, mainFragment, FRAGMENT_MAIN)
            }
        }
    }

    private fun FragmentTransaction.hideCurrent() {
        fragmentManager.findFragmentById(containerId)?.let { hide(it) }
    }

    fun back(): Boolean {
        return if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            true
        } else {
            false
        }
    }

    fun openSearchFragment() {
        fragmentManager.commit {
            hideCurrent()
            add(containerId, SearchFragment.newInstance(), FRAGMENT_SEARCH)
            addToBackStack(FRAGMENT_SEARCH)
        }
    }

    fun openCompanyFragment(ticker: String) {
        fragmentManager.apply {
            commit {
                hideCurrent()
                add(containerId, CompanyFragment.newInstance(ticker), FRAGMENT_COMPANY)
                addToBackStack(FRAGMENT_COMPANY)
            }
        }
    }

    fun openWebViewFragment(url: String) {
        fragmentManager.commit {
            hideCurrent()
            add(containerId, WebViewFragment.newInstance(url), FRAGMENT_WEBVIEW)
            addToBackStack(FRAGMENT_WEBVIEW)
        }
    }
}

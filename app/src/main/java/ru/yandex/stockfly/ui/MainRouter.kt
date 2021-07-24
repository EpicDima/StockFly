package ru.yandex.stockfly.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.yandex.stockfly.ui.company.CompanyFragment
import ru.yandex.stockfly.ui.main.MainFragment
import ru.yandex.stockfly.ui.search.SearchFragment
import ru.yandex.stockfly.ui.web.WebViewFragment

class MainRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    private enum class FragmentPage {
        MAIN, SEARCH, COMPANY, WEBVIEW;

        val tag: String = toString()
    }

    init {
        if (fragmentManager.fragments.isEmpty()) {
            val mainFragment = MainFragment.newInstance()
            fragmentManager.commit {
                openFragment(mainFragment, FragmentPage.MAIN, false)
            }
        }
    }

    private fun FragmentTransaction.openFragment(
        fragment: Fragment,
        page: FragmentPage,
        toBackStack: Boolean = true,
        changeTagFunction: (tag: String) -> String = { tag -> tag }
    ) {
        val tag = changeTagFunction(page.tag)

        replace(containerId, fragment, tag)

        if (toBackStack) {
            addToBackStack(tag)
        }
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
            openFragment(SearchFragment.newInstance(), FragmentPage.SEARCH)
        }
    }

    fun openCompanyFragment(ticker: String) {
        val companyTag = FragmentPage.COMPANY.tag + ticker

        if (fragmentManager.findFragmentByTag(companyTag) != null) {
            fragmentManager.popBackStack(companyTag, 0)
        } else {
            fragmentManager.commit {
                openFragment(
                    CompanyFragment.newInstance(ticker),
                    FragmentPage.COMPANY,
                    changeTagFunction = { companyTag })
            }
        }
    }

    fun openWebViewFragment(url: String) {
        fragmentManager.commit {
            openFragment(WebViewFragment.newInstance(url), FragmentPage.WEBVIEW)
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
}
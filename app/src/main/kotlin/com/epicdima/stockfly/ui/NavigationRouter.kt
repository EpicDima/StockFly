package com.epicdima.stockfly.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import com.epicdima.stockfly.feature.details.CompanyFragment
import com.epicdima.stockfly.feature.list.ListFragment
import com.epicdima.stockfly.feature.list.search.SearchFragment
import timber.log.Timber

class NavigationRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    private enum class FragmentPage {
        MAIN, SEARCH, COMPANY;

        val tag: String = toString()
    }

    init {
        Timber.v("init")

        fragmentManager.addOnBackStackChangedListener {
            Timber.i("OnBackStackChangedListener %s", fragmentManager.fragments)

            fragmentManager.commit {
                fragmentManager.fragments.dropLast(1).forEach {
                    hide(it)
                    setMaxLifecycle(it, Lifecycle.State.STARTED)
                }

                fragmentManager.fragments.lastOrNull()?.let {
                    show(it)
                    setMaxLifecycle(it, Lifecycle.State.RESUMED)
                }
            }
        }

        if (fragmentManager.fragments.isEmpty()) {
            Timber.i("new MainFragment")

            val listFragment = ListFragment.newInstance()
            fragmentManager.commit {
                openFragment(listFragment, FragmentPage.MAIN, false)
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

        Timber.i(
            "openFragment(fragment: %s, page: %s, toBackStack: %s, tag: %s)",
            fragment,
            page,
            toBackStack,
            tag
        )

        add(containerId, fragment, tag)

        if (toBackStack) {
            addToBackStack(tag)
        }
    }

    fun back(): Boolean {
        Timber.i("back")

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
}
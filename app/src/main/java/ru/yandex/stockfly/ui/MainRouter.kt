package ru.yandex.stockfly.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import ru.yandex.stockfly.ui.company.CompanyFragment
import ru.yandex.stockfly.ui.main.MainFragment
import ru.yandex.stockfly.ui.search.SearchFragment

private const val FRAGMENT_MAIN = "main"
private const val FRAGMENT_SEARCH = "search"
private const val FRAGMENT_COMPANY = "company"

class MainRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    private val mainFragment: Fragment = createMainFragment()

    private fun createMainFragment(): Fragment {
        var fragment = getFragment(FRAGMENT_MAIN)
        if (fragment == null) {
            fragment = MainFragment.newInstance()
            fragmentManager.commitNow {
                add(containerId, fragment, FRAGMENT_MAIN)
            }
        }
        return fragment
    }

    private fun getFragment(name: String): Fragment? {
        return fragmentManager.findFragmentByTag(name)
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
            hide(mainFragment)
            add(containerId, SearchFragment.newInstance(), FRAGMENT_SEARCH)
            addToBackStack(FRAGMENT_SEARCH)
        }
    }

    fun openCompanyFragment(ticker: String) {
        fragmentManager.apply {
            val searchFragment = getFragment(FRAGMENT_SEARCH)
            commit {
                if (mainFragment.isVisible) {
                    hide(mainFragment)
                } else if (searchFragment != null) {
                    hide(searchFragment)
                }
                add(containerId, CompanyFragment.newInstance(ticker), FRAGMENT_COMPANY)
                addToBackStack(FRAGMENT_COMPANY)
            }
        }
    }
}

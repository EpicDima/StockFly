package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.BaseFragment
import com.epicdima.stockfly.databinding.FragmentMainBinding
import com.epicdima.stockfly.other.customize
import com.epicdima.stockfly.other.getDimensionInSp
import com.epicdima.stockfly.other.set
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.all.AllMainTabFragment
import com.epicdima.stockfly.ui.main.favourite.FavouriteMainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    companion object {

        const val ALL_TAB_NUMBER = 0
        const val FAVOURITE_TAB_NUMBER = 1

        @JvmStatic
        fun newInstance(): MainFragment {
            Timber.i("newInstance")
            return MainFragment()
        }
    }

    private lateinit var titles: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")

        titles = resources.getStringArray(R.array.main_tabs)
        _binding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewPager.adapter = MainFragmentAdapter(titles.size, this@MainFragment)
            viewPager.isUserInputEnabled = false
            searchLayout.setOnClickListener {
                (requireActivity() as MainRouter.SearchFragmentOpener).openSearchFragment()
            }
        }
        setupTabs()
        return binding.root
    }

    private fun setupTabs() {
        binding.apply {
            tabLayout.customize(viewPager, R.layout.main_tab_item_layout, titles, onSelect = {
                it.set(
                    resources.getDimensionInSp(R.dimen.main_tab_selected_textsize),
                    R.color.black
                )
            }, onUnselect = {
                it.set(resources.getDimensionInSp(R.dimen.main_tab_textsize), R.color.dark)
            })
            tabLayout.getTabAt(0)?.select()
        }
    }


    private class MainFragmentAdapter(
        private val tabsCount: Int,
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return tabsCount
        }

        override fun createFragment(position: Int): Fragment {
            Timber.i("createFragment with position %d", position)

            return when (position) {
                ALL_TAB_NUMBER -> AllMainTabFragment.newInstance(position)
                FAVOURITE_TAB_NUMBER -> FavouriteMainTabFragment.newInstance(position)
                else -> throw RuntimeException("Unknown main tab fragment on position '$position'")
            }
        }
    }
}

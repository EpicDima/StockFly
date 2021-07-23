package ru.yandex.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentMainBinding
import ru.yandex.stockfly.other.customize
import ru.yandex.stockfly.other.getDimensionInSp
import ru.yandex.stockfly.other.set
import ru.yandex.stockfly.ui.SearchFragmentOpener
import ru.yandex.stockfly.ui.main.all.AllMainTabFragment
import ru.yandex.stockfly.ui.main.favourite.FavouriteMainTabFragment

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {
    companion object {

        const val ALL_TAB_NUMBER = 0
        const val FAVOURITE_TAB_NUMBER = 1

        @JvmStatic
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private lateinit var titles: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        titles = resources.getStringArray(R.array.main_tabs)
        _binding = FragmentMainBinding.inflate(inflater, container, false).apply {
            viewPager.adapter = MainFragmentAdapter(titles.size, this@MainFragment)
            viewPager.isUserInputEnabled = false
            searchLayout.setOnClickListener {
                (requireActivity() as SearchFragmentOpener).openSearchFragment()
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
            return when (position) {
                ALL_TAB_NUMBER -> AllMainTabFragment.newInstance(position)
                FAVOURITE_TAB_NUMBER -> FavouriteMainTabFragment.newInstance(position)
                else -> throw RuntimeException("Unknown main tab fragment on position '$position'")
            }
        }
    }
}

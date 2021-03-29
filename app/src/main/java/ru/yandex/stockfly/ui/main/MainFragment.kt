package ru.yandex.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentMainBinding
import ru.yandex.stockfly.other.getDimensionInSp
import ru.yandex.stockfly.other.set
import ru.yandex.stockfly.ui.SearchFragmentOpener

@AndroidEntryPoint
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {
    companion object {
        @JvmStatic
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override val viewModel: MainViewModel by viewModels()
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
        viewModel.refreshCompanies()
        return binding.root
    }

    private fun setupTabs() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.set(
                        resources.getDimensionInSp(R.dimen.main_tab_selected_textsize),
                        R.color.black
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.set(resources.getDimensionInSp(R.dimen.main_tab_textsize), R.color.dark)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.customView = View.inflate(context, R.layout.main_tab_item_layout, null)
                tab.text = titles[position]
                viewPager.currentItem = tab.position
            }.attach()
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
            return MainTabFragment.newInstance(position)
        }
    }
}

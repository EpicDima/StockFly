package ru.yandex.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentMainBinding
import ru.yandex.stockfly.other.customize
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
            return MainTabFragment.newInstance(position)
        }
    }
}

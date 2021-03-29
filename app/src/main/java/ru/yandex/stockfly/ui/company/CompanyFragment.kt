package ru.yandex.stockfly.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentCompanyBinding
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.other.getDimensionInSp
import ru.yandex.stockfly.other.set
import ru.yandex.stockfly.ui.company.chart.ChartFragment
import ru.yandex.stockfly.ui.company.news.NewsFragment
import ru.yandex.stockfly.ui.company.summary.SummaryFragment

@AndroidEntryPoint
class CompanyFragment : BaseFragment<CompanyViewModel, FragmentCompanyBinding>() {
    companion object {
        const val TICKER_KEY = "ticker"

        @JvmStatic
        fun newInstance(ticker: String): CompanyFragment {
            return CompanyFragment().apply {
                arguments = Bundle().apply {
                    putString(TICKER_KEY, ticker)
                }
            }
        }
    }

    override val viewModel: CompanyViewModel by viewModels()

    private lateinit var titles: Array<String>

    private lateinit var createAdapterObserver: Observer<Company>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        titles = resources.getStringArray(R.array.company_tabs)
        _binding = FragmentCompanyBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            error = viewModel.error
            company = viewModel.company
            viewPager.isUserInputEnabled = false
        }
        setSingleEventForTabAdapterCreation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.favouriteButton.setOnClickListener {
            viewModel.changeFavourite()
        }
    }

    private fun setSingleEventForTabAdapterCreation() {
        createAdapterObserver = Observer<Company> {
            binding.viewPager.adapter =
                CompanyFragmentAdapter(
                    requireArguments().getString(TICKER_KEY)!!,
                    titles.size,
                    this@CompanyFragment
                )
            setupTabs()
            viewModel.company.removeObserver(createAdapterObserver)
        }
        viewModel.company.observe(viewLifecycleOwner, createAdapterObserver)
    }

    private fun setupTabs() {
        binding.apply {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.set(
                        resources.getDimensionInSp(R.dimen.company_tab_selected_textsize),
                        R.color.black
                    )
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.set(resources.getDimensionInSp(R.dimen.company_tab_textsize), R.color.dark)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.customView = View.inflate(context, R.layout.company_tab_item_layout, null)
                tab.text = titles[position]
                viewPager.currentItem = tab.position
            }.attach()
            tabLayout.getTabAt(0)?.select()
        }
    }


    private class CompanyFragmentAdapter(
        private val ticker: String,
        private val tabsCount: Int,
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return tabsCount
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ChartFragment.newInstance(ticker)
                1 -> SummaryFragment.newInstance(ticker)
                2 -> NewsFragment.newInstance(ticker)
                else -> throw RuntimeException("Unknown company tab fragment on position '$position'")
            }
        }
    }
}
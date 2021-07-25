package com.epicdima.stockfly.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentCompanyBinding
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.other.customize
import com.epicdima.stockfly.other.getDimensionInSp
import com.epicdima.stockfly.other.set
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.company.chart.ChartFragment
import com.epicdima.stockfly.ui.company.news.NewsFragment
import com.epicdima.stockfly.ui.company.recomendation.RecommendationFragment
import com.epicdima.stockfly.ui.company.summary.SummaryFragment
import timber.log.Timber

@AndroidEntryPoint
class CompanyFragment : BaseViewModelFragment<CompanyViewModel, FragmentCompanyBinding>() {
    companion object {
        const val TICKER_KEY = "ticker"

        private const val CHART_TAB_NUMBER = 0
        private const val SUMMARY_TAB_NUMBER = 1
        private const val NEWS_TAB_NUMBER = 2
        private const val RECOMMENDATION_TAB_NUMBER = 3

        @JvmStatic
        fun newInstance(ticker: String): CompanyFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return CompanyFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: CompanyViewModel by viewModels()

    private lateinit var titles: Array<String>

    private lateinit var createAdapterObserver: Observer<Company>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        titles = resources.getStringArray(R.array.company_tabs)
        _binding = FragmentCompanyBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            error = viewModel.error
            company = viewModel.company
            viewPager.isUserInputEnabled = false
        }
        setSingleEventForTabAdapterCreation()
        setupClickListeners()
        return binding.root
    }

    private fun setSingleEventForTabAdapterCreation() {
        Timber.v("setSingleEventForTabAdapterCreation")
        createAdapterObserver = Observer<Company> {
            binding.viewPager.adapter =
                CompanyFragmentAdapter(
                    requireArguments().getString(TICKER_KEY)!!,
                    titles.size,
                    this@CompanyFragment
                )
            setupTabs()
            viewModel.company.removeObserver(createAdapterObserver)
            binding.company = viewModel.company
        }
        viewModel.company.observe(viewLifecycleOwner, createAdapterObserver)
    }

    private fun setupTabs() {
        binding.apply {
            tabLayout.customize(viewPager, R.layout.company_tab_item_layout, titles, onSelect = {
                it.set(
                    resources.getDimensionInSp(R.dimen.company_tab_selected_textsize),
                    R.color.black
                )
            }, onUnselect = {
                it.set(resources.getDimensionInSp(R.dimen.company_tab_textsize), R.color.dark)
            })
            tabLayout.getTabAt(0)?.select()
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.favouriteButton.setOnClickListener {
            viewModel.changeFavourite()
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
            Timber.i("createFragment with position %d", position)
            return when (position) {
                CHART_TAB_NUMBER -> ChartFragment.newInstance(ticker)
                SUMMARY_TAB_NUMBER -> SummaryFragment.newInstance(ticker)
                NEWS_TAB_NUMBER -> NewsFragment.newInstance(ticker)
                RECOMMENDATION_TAB_NUMBER -> RecommendationFragment.newInstance(ticker)
                else -> throw RuntimeException("Unknown company tab fragment on position '$position'")
            }
        }
    }
}
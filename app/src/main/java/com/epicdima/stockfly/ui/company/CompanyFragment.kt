package com.epicdima.stockfly.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CompanyFragment : BaseViewModelFragment<CompanyViewModel, FragmentCompanyBinding>() {

    companion object {
        const val TICKER_KEY = "ticker"
        private const val PAGE_KEY = "page"

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
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        titles = CompanyTab.values().map { resources.getString(it.titleId) }.toTypedArray()
        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
            adapter = CompanyFragmentAdapter(
                requireArguments().getString(TICKER_KEY)!!,
                this@CompanyFragment
            )
        }
        viewModel.error.observe(viewLifecycleOwner) {
            binding.errorTextview.isVisible = it
            binding.favouriteButton.visibility =
                if (viewModel.company.value != null && it != true) View.VISIBLE else View.INVISIBLE
            binding.tabLayout.isVisible = (viewModel.company.value != null && it != true)
            binding.viewPager.isVisible = (viewModel.company.value != null && it != true)
            binding.progressBar.isVisible = (viewModel.company.value == null && it != true)
        }
        viewModel.company.observe(viewLifecycleOwner) {
            setCompany(it)
        }

        setSingleEventForTabAdapterCreation()
        setupClickListeners()
        setupTabs()
    }

    private fun setCompany(company: Company?) {
        binding.ticker.text = company?.ticker ?: ""
        binding.name.text = company?.name ?: ""
        binding.favouriteButton.visibility =
            if (company != null && viewModel.error.value != true) View.VISIBLE else View.INVISIBLE
        binding.favouriteIcon.setImageResource(if (company?.favourite == true) R.drawable.ic_star_selected else R.drawable.ic_star)
        binding.tabLayout.isVisible = (company != null && viewModel.error.value != true)
        binding.viewPager.isVisible = (company != null && viewModel.error.value != true)
        binding.progressBar.isVisible = (company == null && viewModel.error.value != true)
    }

    private fun setSingleEventForTabAdapterCreation() {
        Timber.v("setSingleEventForTabAdapterCreation")
        createAdapterObserver = Observer<Company> {
            viewModel.company.removeObserver(createAdapterObserver)
            setCompany(viewModel.company.value)
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

    override fun onResume() {
        super.onResume()
        binding.viewPager.setCurrentItem(arguments?.getInt(PAGE_KEY) ?: 0, false)
    }

    override fun onPause() {
        arguments?.putInt(PAGE_KEY, binding.viewPager.currentItem)
        super.onPause()
    }


    private enum class CompanyTab(
        @StringRes
        val titleId: Int
    ) {
        CHART(R.string.chart),
        SUMMARY(R.string.summary),
        NEWS(R.string.news),
        RECOMMENDATION(R.string.recommendations)
    }


    private class CompanyFragmentAdapter(
        private val ticker: String,
        fragment: Fragment
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return CompanyTab.values().size
        }

        override fun createFragment(position: Int): Fragment {
            Timber.i("createFragment with position %d", position)
            return when (position) {
                CompanyTab.CHART.ordinal -> ChartFragment.newInstance(ticker)
                CompanyTab.SUMMARY.ordinal -> SummaryFragment.newInstance(ticker)
                CompanyTab.NEWS.ordinal -> NewsFragment.newInstance(ticker)
                CompanyTab.RECOMMENDATION.ordinal -> RecommendationFragment.newInstance(ticker)
                else -> throw RuntimeException("Unknown company tab fragment on position '$position'")
            }
        }
    }
}
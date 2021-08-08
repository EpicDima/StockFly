package com.epicdima.stockfly.ui.company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewModelFragment
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
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class CompanyFragment : ViewModelFragment<CompanyViewModel, FragmentCompanyBinding>() {

    companion object {
        const val TICKER_KEY = "ticker"
        private const val PAGE_KEY = "page"

        @JvmStatic
        fun newInstance(ticker: String): CompanyFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return CompanyFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: CompanyViewModel by viewModels()

    private lateinit var titles: Array<String>

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCompanyBinding {
        return FragmentCompanyBinding.inflate(inflater, container, false)
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
        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.errorWidget.root.isVisible = it
                binding.favouriteButton.visibility =
                    if (viewModel.company.value != null && !it) View.VISIBLE else View.INVISIBLE
                binding.tabLayout.isVisible = (viewModel.company.value != null && !it)
                binding.viewPager.isVisible = (viewModel.company.value != null && !it)
                binding.progressBarWidget.root.isVisible =
                    (viewModel.company.value == null && !it)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.company
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { setCompany(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setupClickListeners()
        setupTabs()
    }

    private fun setCompany(company: Company?) {
        binding.ticker.text = company?.ticker ?: ""
        binding.name.text = company?.name ?: ""
        binding.favouriteButton.visibility =
            if (company != null && !viewModel.error.value) View.VISIBLE else View.INVISIBLE
        binding.favouriteIcon.setImageResource(if (company?.favourite == true) R.drawable.ic_star_selected else R.drawable.ic_star)
        binding.tabLayout.isVisible = (company != null && !viewModel.error.value)
        binding.viewPager.isVisible = (company != null && !viewModel.error.value)
        binding.progressBarWidget.root.isVisible =
            (company == null && !viewModel.error.value)
    }

    private fun setupTabs() {
        binding.apply {
            tabLayoutMediator = tabLayout.customize(
                viewPager,
                R.layout.company_tab_item_layout,
                titles,
                onSelect = {
                    it.set(
                        resources.getDimensionInSp(R.dimen.company_tab_selected_textsize),
                        R.color.black
                    )
                },
                onUnselect = {
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

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
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
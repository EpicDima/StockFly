package com.epicdima.stockfly.feature.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epicdima.stockfly.core.common.ViewModelFragment
import com.epicdima.stockfly.core.common.setArgument
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.core.ui.customize
import com.epicdima.stockfly.core.ui.getDimensionInSp
import com.epicdima.stockfly.core.ui.set
import com.epicdima.stockfly.feature.details.chart.ChartFragment
import com.epicdima.stockfly.feature.details.databinding.FragmentCompanyBinding
import com.epicdima.stockfly.feature.details.news.NewsFragment
import com.epicdima.stockfly.feature.details.recommendations.RecommendationsFragment
import com.epicdima.stockfly.feature.details.summary.SummaryFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class DetailsFragment : ViewModelFragment<DetailsViewModel, FragmentCompanyBinding>() {

    companion object {
        const val TICKER_KEY = "ticker"
        private const val PAGE_KEY = "page"

        @JvmStatic
        fun newInstance(ticker: String): DetailsFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return DetailsFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: DetailsViewModel by viewModels()

    private val titles: Array<String> by lazy(LazyThreadSafetyMode.NONE) {
        CompanyTab.values().map { resources.getString(it.titleId) }.toTypedArray()
    }

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun getLayoutId(): Int = R.layout.fragment_company

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentCompanyBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentCompanyBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.apply {
            isUserInputEnabled = false
            offscreenPageLimit = 1
            adapter = CompanyFragmentAdapter(
                requireArguments().getString(TICKER_KEY)!!,
                this@DetailsFragment
            )
        }
        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.apply {
                    errorWidget.root.isVisible = it
                    favouriteButton.visibility =
                        if (viewModel.company.value != null && !it) View.VISIBLE else View.INVISIBLE
                    tabLayout.isVisible = (viewModel.company.value != null && !it)
                    viewPager.isVisible = (viewModel.company.value != null && !it)
                    progressBarWidget.root.isVisible = (viewModel.company.value == null && !it)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.company
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach { setCompany(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setupClickListeners()
        setupTabs()
    }

    private fun setCompany(company: Company?) {
        binding.apply {
            ticker.text = company?.ticker ?: ""
            name.text = company?.name ?: ""
            favouriteButton.visibility =
                if (company != null && !viewModel.error.value) View.VISIBLE else View.INVISIBLE
            favouriteIcon.setImageResource(if (company?.favourite == true) com.epicdima.stockfly.core.ui.R.drawable.ic_star_selected else com.epicdima.stockfly.core.ui.R.drawable.ic_star)
            tabLayout.isVisible = (company != null && !viewModel.error.value)
            viewPager.isVisible = (company != null && !viewModel.error.value)
            progressBarWidget.root.isVisible = (company == null && !viewModel.error.value)
        }
    }

    private fun setupTabs() {
        binding.apply {
            tabLayoutMediator = tabLayout.customize(
                viewPager,
                R.layout.company_tab_item_layout,
                titles,
                onSelect = {
                    it.set(
                        resources.getDimensionInSp(com.epicdima.stockfly.core.ui.R.dimen.company_tab_selected_textsize),
                        com.epicdima.stockfly.core.ui.R.color.black
                    )
                },
                onUnselect = {
                    it.set(
                        resources.getDimensionInSp(com.epicdima.stockfly.core.ui.R.dimen.company_tab_textsize),
                        com.epicdima.stockfly.core.ui.R.color.dark
                    )
                })
            tabLayout.getTabAt(0)?.select()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            backButton.setOnClickListener {
                requireActivity().onBackPressed()
            }

            favouriteButton.setOnClickListener {
                viewModel.changeFavourite()
            }
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
                CompanyTab.RECOMMENDATION.ordinal -> RecommendationsFragment.newInstance(ticker)
                else -> throw RuntimeException("Unknown company tab fragment on position '$position'")
            }
        }
    }
}
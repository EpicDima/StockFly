package com.epicdima.stockfly.ui.main.all

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.R
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.core.navigation.CompanyFragmentOpener
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.CompanyAdapter
import com.epicdima.stockfly.ui.main.MainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class AllMainTabFragment : MainTabFragment<AllMainTabViewModel>() {

    companion object {

        @JvmStatic
        fun newInstance(): AllMainTabFragment {
            Timber.i("newInstance")
            return AllMainTabFragment()
        }
    }

    override val viewModel: AllMainTabViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emptyTextview.setText(R.string.stocks_empty_list)
    }

    override fun createAdapter(): CompanyAdapter {
        return CompanyAdapter(formatter, this::showPopupMenu) { ticker ->
            (requireParentFragment().requireActivity() as CompanyFragmentOpener)
                .openCompanyFragment(ticker)
        }
    }

    override fun setupList() {
        viewModel.companies
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                companyAdapter.submitCompanyList(it, requireContext())
            }
            .flowOn(Dispatchers.Default)
            .onEach {
                binding.apply {
                    recyclerView.isVisible = it.isNotEmpty()
                    emptyTextview.isVisible = it.isEmpty()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun changeFavourite(company: Company) {
        viewModel.changeFavourite(company)
    }

    override fun deleteCompany(company: Company) {
        viewModel.deleteCompany(company)
    }
}
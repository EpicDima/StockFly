package com.epicdima.stockfly.ui.main.all

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.R
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.ui.MainRouter
import com.epicdima.stockfly.ui.main.CompanyAdapter
import com.epicdima.stockfly.ui.main.MainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
        return CompanyAdapter(formatter, { view, company ->
            openPopUpMenu(view, company)
        }) { ticker ->
            (requireParentFragment().requireActivity() as MainRouter.CompanyFragmentOpener)
                .openCompanyFragment(ticker)
        }
    }

    override fun setupList() {
        viewModel.companies
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    companyAdapter.submitCompanyList(it, requireContext())
                }
                binding.apply {
                    recyclerView.isVisible = it.isNotEmpty()
                    emptyTextview.isVisible = it.isEmpty()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun openPopUpMenu(view: View, company: Company) {
        PopupMenu(requireContext(), view).apply {
            menu.add(
                1, 1, 1,
                if (company.favourite) R.string.companies_popup_menu_item_set_unfavourite
                else R.string.companies_popup_menu_item_set_favourite
            )
            menu.add(1, 2, 2, R.string.companies_popup_menu_item_delete)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    1 -> viewModel.changeFavourite(company)
                    2 -> viewModel.deleteCompany(company)
                    else -> throw RuntimeException("Unknown company popup menu item id: ${it.itemId}")
                }
                true
            }
        }.show()
    }
}
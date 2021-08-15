package com.epicdima.stockfly.ui.main.favourite

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
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
class FavouriteMainTabFragment : MainTabFragment<FavouriteMainTabViewModel>() {

    companion object {

        @JvmStatic
        fun newInstance(): FavouriteMainTabFragment {
            Timber.i("newInstance")
            return FavouriteMainTabFragment()
        }
    }

    override val viewModel: FavouriteMainTabViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emptyTextview.setText(R.string.favourite_empty_list)
    }

    override fun createAdapter(): CompanyAdapter {
        return CompanyAdapter(formatter, { view, company ->
            showPopupMenu(view, company)
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

        ItemTouchHelper(FavouriteCompanyDragCallback { from, to ->
            viewModel.changeFavouriteNumber(from, to)
            companyAdapter.notifyItemMoved(from, to)
        }).attachToRecyclerView(binding.recyclerView)
    }

    override fun changeFavourite(company: Company) {
        viewModel.changeFavourite(company)
    }

    override fun deleteCompany(company: Company) {
        viewModel.deleteCompany(company)
    }
}
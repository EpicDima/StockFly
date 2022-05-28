package com.epicdima.stockfly.feature.list.favourite

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.epicdima.stockfly.core.model.Company
import com.epicdima.stockfly.feature.list.shared.CompanyAdapter
import com.epicdima.stockfly.feature.list.shared.ListTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@AndroidEntryPoint
class FavouriteListTabFragment : ListTabFragment<FavouriteListTabViewModel>() {

    companion object {

        @JvmStatic
        fun newInstance(): FavouriteListTabFragment {
            Timber.i("newInstance")
            return FavouriteListTabFragment()
        }
    }

    override val viewModel: FavouriteListTabViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emptyTextview.setText(R.string.favourite_empty_list)
    }

    override fun createAdapter(): CompanyAdapter {
        return CompanyAdapter(
            formatter,
            this::showPopupMenu,
            viewModel::openCompanyDetails
        )
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
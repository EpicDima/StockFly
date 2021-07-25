package com.epicdima.stockfly.ui.main.favourite

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.main.MainTabFragment
import timber.log.Timber

@AndroidEntryPoint
class FavouriteMainTabFragment : MainTabFragment<FavouriteMainTabViewModel>() {
    companion object {
        @JvmStatic
        fun newInstance(tabNumber: Int): FavouriteMainTabFragment {
            Timber.i("newInstance with tabNumber %d", tabNumber)
            return FavouriteMainTabFragment().setArgument(TAB_NUMBER_KEY, tabNumber)
        }
    }

    override val _viewModel: FavouriteMainTabViewModel by viewModels()

    override fun setupList() {
        viewModel.companies.observe(viewLifecycleOwner) {
            adapter.submitCompanyList(it)
            binding.empty = it.isEmpty()
        }
        ItemTouchHelper(FavouriteCompanyDragCallback { from, to ->
            viewModel.changeFavouriteNumber(from, to)
            adapter.notifyItemMoved(from, to)
        }).attachToRecyclerView(binding.recyclerView)
    }
}
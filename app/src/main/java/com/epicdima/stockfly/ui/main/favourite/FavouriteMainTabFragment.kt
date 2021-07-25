package com.epicdima.stockfly.ui.main.favourite

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.main.MainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            lifecycleScope.launch(Dispatchers.Default) {
                adapter.submitCompanyList(it)
            }
            binding.recyclerView.isVisible = it.isNotEmpty()
            binding.emptyTextview.isVisible = it.isEmpty()
        }
        ItemTouchHelper(FavouriteCompanyDragCallback { from, to ->
            viewModel.changeFavouriteNumber(from, to)
            adapter.notifyItemMoved(from, to)
        }).attachToRecyclerView(binding.recyclerView)
    }
}
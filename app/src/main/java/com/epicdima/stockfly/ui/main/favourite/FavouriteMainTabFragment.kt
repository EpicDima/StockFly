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
import com.epicdima.stockfly.ui.main.MainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
}
package com.epicdima.stockfly.ui.main.all

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.main.MainTabFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AllMainTabFragment : MainTabFragment<AllMainTabViewModel>() {

    companion object {

        @JvmStatic
        fun newInstance(tabNumber: Int): AllMainTabFragment {
            Timber.i("newInstance with tabNumber %d", tabNumber)
            return AllMainTabFragment().setArgument(TAB_NUMBER_KEY, tabNumber)
        }
    }

    override val _viewModel: AllMainTabViewModel by viewModels()

    override fun setupList() {
        viewModel.companies.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Default) {
                adapter.submitCompanyList(it)
            }
            binding.recyclerView.isVisible = it.isNotEmpty()
            binding.emptyTextview.isVisible = it.isEmpty()
        }
    }
}
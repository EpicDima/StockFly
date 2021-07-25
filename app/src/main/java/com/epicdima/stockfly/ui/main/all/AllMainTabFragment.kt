package com.epicdima.stockfly.ui.main.all

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.main.MainTabFragment
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
            adapter.submitCompanyList(it)
            binding.empty = it.isEmpty()
        }
    }
}
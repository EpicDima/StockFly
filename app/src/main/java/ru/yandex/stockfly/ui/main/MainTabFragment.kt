package ru.yandex.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseViewModelFragment
import ru.yandex.stockfly.databinding.FragmentTabMainBinding
import ru.yandex.stockfly.other.setArgument
import ru.yandex.stockfly.ui.CompanyFragmentOpener

@AndroidEntryPoint
class MainTabFragment : BaseViewModelFragment<MainTabViewModel, FragmentTabMainBinding>() {
    companion object {
        const val TAB_NUMBER_KEY = "tab_number"

        const val STOCKS_TAB_NUMBER = 0
        const val FAVOURITE_TAB_NUMBER = 1

        @JvmStatic
        fun newInstance(tabNumber: Int): MainTabFragment {
            return MainTabFragment().setArgument(TAB_NUMBER_KEY, tabNumber)
        }
    }

    override val _viewModel: MainTabViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabMainBinding.inflate(inflater, container, false).apply {
            emptyTextview.text =
                resources.getStringArray(R.array.main_tabs_empty_list)[getTabNumber()]
        }
        setupList()
        return binding.root
    }

    private fun setupList() {
        val adapter = CompanyAdapter { ticker ->
            (requireParentFragment().requireActivity() as CompanyFragmentOpener)
                .openCompanyFragment(ticker)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            setHasFixedSize(true)
        }
        if (getTabNumber() == FAVOURITE_TAB_NUMBER) {
            ItemTouchHelper(FavouriteCompanyDragCallback { from, to ->
                viewModel.changeFavouriteNumber(from, to)
                adapter.notifyItemMoved(from, to)
            }).attachToRecyclerView(binding.recyclerView)
        }
        viewModel.companies.observe(viewLifecycleOwner) {
            adapter.submitCompanyList(it)
            binding.empty = it.isEmpty()
        }
    }

    private fun getTabNumber(): Int {
        return requireArguments().getInt(TAB_NUMBER_KEY)
    }
}
package ru.yandex.stockfly.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.R
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentTabMainBinding
import ru.yandex.stockfly.ui.CompanyFragmentOpener

@AndroidEntryPoint
class MainTabFragment : BaseFragment<MainTabViewModel, FragmentTabMainBinding>() {
    companion object {
        const val TAB_NUMBER_KEY = "tab_number"

        const val STOCKS_TAB_NUMBER = 0
        const val FAVOURITE_TAB_NUMBER = 1

        @JvmStatic
        fun newInstance(tabNumber: Int): MainTabFragment {
            return MainTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(TAB_NUMBER_KEY, tabNumber)
                }
            }
        }
    }

    override val viewModel: MainTabViewModel by viewModels()

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

    @SuppressLint("NotifyDataSetChanged")
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
        viewModel.companies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (getTabNumber() == FAVOURITE_TAB_NUMBER) {
                // по причине того, что цвет фона связан с позицией,
                // которая присваивается в onBind и пока самый простой способ сделать так
                adapter.notifyDataSetChanged()
            }
            binding.empty = it.isEmpty()
        }
    }

    private fun getTabNumber(): Int {
        return requireArguments().getInt(TAB_NUMBER_KEY)
    }
}
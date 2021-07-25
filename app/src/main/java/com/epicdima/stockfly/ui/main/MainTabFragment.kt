package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentTabMainBinding
import com.epicdima.stockfly.ui.MainRouter

abstract class MainTabFragment<VM : ViewModel> :
    BaseViewModelFragment<VM, FragmentTabMainBinding>() {
    companion object {
        const val TAB_NUMBER_KEY = "main_tab_number"
    }

    protected val adapter = CompanyAdapter { ticker ->
        (requireParentFragment().requireActivity() as MainRouter.CompanyFragmentOpener)
            .openCompanyFragment(ticker)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabMainBinding.inflate(inflater, container, false).apply {
            recyclerView.apply {
                adapter = this@MainTabFragment.adapter
                layoutManager = LinearLayoutManager(context)
                itemAnimator = null
                setHasFixedSize(true)
            }
            emptyTextview.text =
                resources.getStringArray(R.array.main_tabs_empty_list)[getTabNumber()]
        }
        setupList()
        return binding.root
    }

    private fun getTabNumber(): Int {
        return requireArguments().getInt(TAB_NUMBER_KEY)
    }

    protected abstract fun setupList()
}
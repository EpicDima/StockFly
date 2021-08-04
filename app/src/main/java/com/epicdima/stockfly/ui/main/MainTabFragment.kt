package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentTabMainBinding
import com.epicdima.stockfly.ui.MainRouter
import timber.log.Timber

abstract class MainTabFragment<VM : ViewModel> :
    BaseViewModelFragment<VM, FragmentTabMainBinding>() {

    protected val companyAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CompanyAdapter { ticker ->
            (requireParentFragment().requireActivity() as MainRouter.CompanyFragmentOpener)
                .openCompanyFragment(ticker)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentTabMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerView.apply {
                adapter = companyAdapter
                setRecycledViewPool(companyAdapter.recycledViewPool)
                layoutManager = LinearLayoutManager(context)
                itemAnimator = null
                setHasFixedSize(true)
            }
        }
        setupList()
    }

    protected abstract fun setupList()
}
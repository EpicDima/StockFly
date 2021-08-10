package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.databinding.FragmentTabMainBinding
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.ui.MainRouter
import timber.log.Timber
import javax.inject.Inject

abstract class MainTabFragment<VM : ViewModel> :
    ViewModelFragment<VM, FragmentTabMainBinding>() {

    @Inject
    lateinit var formatter: Formatter

    protected lateinit var companyAdapter: CompanyAdapter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTabMainBinding {
        return FragmentTabMainBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        companyAdapter = CompanyAdapter(formatter) { ticker ->
            (requireParentFragment().requireActivity() as MainRouter.CompanyFragmentOpener)
                .openCompanyFragment(ticker)
        }
        return view
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

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}
package com.epicdima.stockfly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.databinding.FragmentTabMainBinding
import com.epicdima.stockfly.di.CompanyList
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.other.Formatter
import javax.inject.Inject

abstract class MainTabFragment<VM : ViewModel> :
    ViewModelFragment<VM, FragmentTabMainBinding>() {

    @Inject
    lateinit var formatter: Formatter

    @Inject
    @CompanyList
    lateinit var companyRecycledViewPool: RecyclerView.RecycledViewPool

    protected lateinit var companyAdapter: CompanyAdapter

    private val addToFavouritesText: String by lazy(LazyThreadSafetyMode.NONE) {
        getString(R.string.companies_popup_menu_item_add_to_favourites)
    }
    private val deleteFromFavouritesText: String by lazy(LazyThreadSafetyMode.NONE) {
        getString(R.string.companies_popup_menu_item_delete_from_favourites)
    }
    private val removeText: String by lazy(LazyThreadSafetyMode.NONE) {
        getString(R.string.companies_popup_menu_item_remove)
    }

    override fun getLayoutId(): Int = R.layout.fragment_tab_main

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentTabMainBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentTabMainBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyAdapter = createAdapter().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.apply {
            recyclerView.apply {
                adapter = companyAdapter
                setRecycledViewPool(companyRecycledViewPool)
                layoutManager = LinearLayoutManager(context)
                itemAnimator = null
                setHasFixedSize(true)
            }
        }
        setupList()
    }

    protected abstract fun createAdapter(): CompanyAdapter

    protected abstract fun setupList()

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    protected fun showPopupMenu(view: View, company: Company) {
        PopupMenu(view.context, view).apply {
            menu.add(
                1, 1, 1,
                if (company.favourite) deleteFromFavouritesText
                else addToFavouritesText
            )
            menu.add(1, 2, 2, removeText)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    1 -> changeFavourite(company)
                    2 -> deleteCompany(company)
                    else -> throw RuntimeException("Unknown company popup menu item id: ${it.itemId}")
                }
                true
            }

            setOnDismissListener {
                view.tag = null
            }

            view.tag = this
        }.show()
    }

    protected abstract fun changeFavourite(company: Company)

    protected abstract fun deleteCompany(company: Company)
}
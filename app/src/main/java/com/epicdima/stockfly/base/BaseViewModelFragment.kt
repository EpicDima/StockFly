package com.epicdima.stockfly.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseViewModelFragment<VM : ViewModel, VDB : ViewBinding> : BaseFragment<VDB>() {

    protected open val _viewModel: VM? = null
    protected val viewModel: VM
        get() = _viewModel!!
}
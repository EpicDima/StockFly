package com.epicdima.stockfly.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseViewModelFragment<VM : ViewModel, VDB : ViewDataBinding> : BaseFragment<VDB>() {

    protected open val _viewModel: VM? = null
    protected val viewModel: VM
        get() = _viewModel!!
}
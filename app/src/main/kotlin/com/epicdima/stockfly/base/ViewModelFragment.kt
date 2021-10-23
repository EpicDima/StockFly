package com.epicdima.stockfly.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class ViewModelFragment<VM : ViewModel, VDB : ViewBinding> : ViewBindingFragment<VDB>() {

    protected abstract val viewModel: VM
}
package com.epicdima.stockfly.base

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment<VDB : ViewDataBinding> : Fragment() {

    protected var _binding: VDB? = null
    protected val binding
        get() = _binding!!

    override fun onDestroyView() {
        Timber.v("onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}
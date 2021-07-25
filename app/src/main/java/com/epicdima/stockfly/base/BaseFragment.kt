package com.epicdima.stockfly.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class BaseFragment<VDB : ViewBinding> : Fragment() {

    protected var _binding: VDB? = null
    protected val binding
        get() = _binding!!

    override fun onDestroyView() {
        Timber.v("onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}
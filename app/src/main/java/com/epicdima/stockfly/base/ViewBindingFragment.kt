package com.epicdima.stockfly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class ViewBindingFragment<VDB : ViewBinding> : Fragment() {

    private var _binding: VDB? = null
    protected val binding
        get() = _binding!!

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VDB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}
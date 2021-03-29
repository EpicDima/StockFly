package ru.yandex.stockfly.base

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

abstract class BaseFragment<VM : ViewModel, VDB : ViewDataBinding> : Fragment() {

    protected abstract val viewModel: VM

    protected var _binding: VDB? = null
    protected val binding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.epicdima.stockfly.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.epicdima.stockfly.other.LayoutPool
import timber.log.Timber
import javax.inject.Inject

abstract class ViewBindingFragment<VDB : ViewBinding> : Fragment() {

    private var _binding: VDB? = null
    protected val binding
        get() = _binding!!

    @Inject
    lateinit var layoutPool: LayoutPool

    protected abstract fun getLayoutId(): Int

    protected abstract fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): VDB

    protected abstract fun bind(
        view: View
    ): VDB

    private fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VDB {
        val view = layoutPool.getLayout(getLayoutId(), container)
        return if (view == null) {
            Timber.v("%-15s %-30s", "inflate", javaClass.simpleName)
            inflate(inflater, container, false)
        } else {
            Timber.v("%-15s %-30s", "bind", javaClass.simpleName)
            bind(view)
        }
    }

    override fun onAttach(context: Context) {
        Timber.v("%-15s %-30s", "onAttach", javaClass.simpleName)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.v("%-15s %-30s", "onCreate", javaClass.simpleName)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("%-15s %-30s", "onCreateView", javaClass.simpleName)
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        Timber.v("%-15s %-30s", "onStart", javaClass.simpleName)
        super.onStart()
    }

    override fun onResume() {
        Timber.v("%-15s %-30s", "onResume", javaClass.simpleName)
        super.onResume()
    }

    override fun onPause() {
        Timber.v("%-15s %-30s", "onPause", javaClass.simpleName)
        super.onPause()
    }

    override fun onStop() {
        Timber.v("%-15s %-30s", "onStop", javaClass.simpleName)
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.v("%-15s %-30s", "onDestroyView", javaClass.simpleName)
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Timber.v("%-15s %-30s", "onDestroy", javaClass.simpleName)
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.v("%-15s %-30s", "onDetach", javaClass.simpleName)
        super.onDetach()
    }
}
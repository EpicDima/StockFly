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
            inflate(inflater, container, false)
        } else {
            bind(view)
        }
    }

    override fun onAttach(context: Context) {
        Timber.v("onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.v("onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onStart() {
        Timber.v("onStart")
        super.onStart()
    }

    override fun onResume() {
        Timber.v("onResume")
        super.onResume()
    }

    override fun onPause() {
        Timber.v("onPause")
        super.onPause()
    }

    override fun onStop() {
        Timber.v("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Timber.v("onDestroyView")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Timber.v("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Timber.v("onDetach")
        super.onDetach()
    }
}
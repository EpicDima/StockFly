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
            logWithCurrentClassName("inflate")
            inflate(inflater, container, false)
        } else {
            logWithCurrentClassName("bind")
            bind(view)
        }
    }

    override fun onAttach(context: Context) {
        logWithCurrentMethodName()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        logWithCurrentMethodName()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logWithCurrentMethodName()
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logWithCurrentMethodName()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        logWithCurrentMethodName()
        super.onStart()
    }

    override fun onResume() {
        logWithCurrentMethodName()
        super.onResume()
    }

    override fun onPause() {
        logWithCurrentMethodName()
        super.onPause()
    }

    override fun onStop() {
        logWithCurrentMethodName()
        super.onStop()
    }

    override fun onDestroyView() {
        logWithCurrentMethodName()
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        logWithCurrentMethodName()
        super.onDestroy()
    }

    override fun onDetach() {
        logWithCurrentMethodName()
        super.onDetach()
    }

    private fun logWithCurrentMethodName() {
        if (Timber.treeCount > 0) {
            logWithCurrentClassName(Thread.currentThread().stackTrace[3].methodName)
        }
    }

    private fun logWithCurrentClassName(message: String) {
        if (Timber.treeCount > 0) {
            Timber.d("%-15s %-30s", message, javaClass.simpleName)
        }
    }
}
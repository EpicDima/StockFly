package com.epicdima.stockfly.ui.web

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.view.isVisible
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewBindingFragment
import com.epicdima.stockfly.databinding.FragmentWebviewBinding
import com.epicdima.stockfly.other.setArgument
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WebViewFragment : ViewBindingFragment<FragmentWebviewBinding>() {

    companion object {
        private const val URL_KEY = "url"

        @JvmStatic
        fun newInstance(url: String): WebViewFragment {
            Timber.i("newInstance with url %s", url)
            return WebViewFragment().setArgument(URL_KEY, url)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_webview

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentWebviewBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentWebviewBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            progressBarWidget.root.isVisible = true
            errorWidget.root.isVisible = false

            webview.apply {
                webViewClient = createWebViewClient(binding)

                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                setBackHandler()
                checkNightMode()
                loadUrl(requireArguments().getString(URL_KEY)!!)
            }
        }
    }

    private fun createWebViewClient(binding: FragmentWebviewBinding): WebViewClient {
        Timber.v("createWebViewClient")

        return object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                binding.progressBarWidget.root.isVisible = false
                binding.webview.isVisible = true
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                if (request.isForMainFrame) {
                    binding.errorWidget.root.isVisible = true
                    binding.webview.isVisible = false
                }
            }
        }
    }

    private fun WebView.checkNightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                settings.forceDark = WebSettings.FORCE_DARK_ON
            }
        }
    }

    private fun WebView.setBackHandler() {
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                if (canGoBack()) {
                    goBack()
                } else {
                    requireActivity().onBackPressed()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        binding.webview.apply {
            setOnKeyListener(null)
            removeAllViews()
            destroy()
        }
        super.onDestroyView()
    }
}
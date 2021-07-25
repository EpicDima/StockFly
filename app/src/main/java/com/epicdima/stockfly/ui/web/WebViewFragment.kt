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
import androidx.fragment.app.Fragment
import com.epicdima.stockfly.databinding.FragmentWebviewBinding
import com.epicdima.stockfly.other.setArgument
import timber.log.Timber

class WebViewFragment : Fragment() {

    companion object {
        private const val URL_KEY = "url"

        @JvmStatic
        fun newInstance(url: String): WebViewFragment {
            Timber.i("newInstance with url %s", url)
            return WebViewFragment().setArgument(URL_KEY, url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")

        val binding = FragmentWebviewBinding.inflate(inflater, container, false).apply {
            progressBar.isVisible = true
            errorTextview.isVisible = false
        }

        binding.webview.apply {
            webViewClient = createWebViewClient(binding)
            settings.javaScriptEnabled = true
            setBackHandler()
            checkNightMode()
            loadUrl(requireArguments().getString(URL_KEY)!!)
        }

        return binding.root
    }

    private fun createWebViewClient(binding: FragmentWebviewBinding): WebViewClient {
        Timber.v("createWebViewClient")

        return object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                binding.progressBar.isVisible = false
                binding.webview.isVisible = true
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                if (request.isForMainFrame) {
                    binding.errorTextview.isVisible = true
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
}
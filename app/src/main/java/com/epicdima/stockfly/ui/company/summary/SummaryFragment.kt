package com.epicdima.stockfly.ui.company.summary

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentSummaryBinding
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.MainRouter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SummaryFragment : BaseViewModelFragment<SummaryViewModel, FragmentSummaryBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_summary"

        @JvmStatic
        fun newInstance(ticker: String): SummaryFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return SummaryFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentSummaryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            company = viewModel.company
            phoneValue.paintFlags = phoneValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            weburlValue.paintFlags = weburlValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.phoneField.setOnClickListener {
            Timber.i("open phone number '%s'", binding.phoneValue.text)
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${binding.phoneValue.text}")))
        }
        binding.weburlField.setOnClickListener {
            val url = binding.weburlValue.text.toString()

            if (url.startsWith("https://")) {
                Timber.i("open safe url '%s'", url)
                (requireParentFragment().requireActivity() as MainRouter.WebViewFragmentOpener).openWebViewFragment(
                    url
                )
            } else {
                Timber.i("open unsafe url '%s'", url)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }
}
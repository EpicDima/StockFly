package com.epicdima.stockfly.ui.company.summary

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.customtabs.CustomTabsProvider
import com.epicdima.stockfly.databinding.FragmentSummaryBinding
import com.epicdima.stockfly.other.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment : ViewModelFragment<SummaryViewModel, FragmentSummaryBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_summary"

        @JvmStatic
        fun newInstance(ticker: String): SummaryFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return SummaryFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: SummaryViewModel by viewModels()

    @Inject
    lateinit var formatter: Formatter

    @Inject
    lateinit var customTabsProvider: CustomTabsProvider

    override fun getLayoutId(): Int = R.layout.fragment_summary

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentSummaryBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentSummaryBinding.bind(view)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            phoneValue.paintFlags = phoneValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            weburlValue.paintFlags = weburlValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        viewModel.company
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                if (it != null) {
                    loadImageWithGoneOnError(binding.imageView, it.logoUrl)
                    binding.apply {
                        fullname.text = it.name
                        phoneValue.text = it.phone
                        weburlValue.text = it.webUrl
                        countryValue.text = it.countryName
                        currencyValue.text = it.currency
                        soValue.text = it.shareOutstanding.toLocalString(formatter)
                        mcValue.text = it.marketCapitalization.toLocalString(formatter)
                        exchangeValue.text = it.exchange
                        ipoValue.text = it.ipoLocalDateString(formatter)
                    }
                    customTabsProvider.mayLaunchUrl(it.webUrl)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.apply {
            phoneValue.setOnClickListener {
                val phoneNumber = phoneValue.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    Timber.i("open phone number '%s'", phoneNumber)
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber}")))
                }
            }

            weburlValue.setOnClickListener {
                val url = weburlValue.text.toString()
                if (url.isNotEmpty()) {
                    customTabsProvider.launchUrl(url)
                }
            }
        }
    }
}
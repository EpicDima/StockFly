package ru.yandex.stockfly.ui.company.summary

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.base.BaseViewModelFragment
import ru.yandex.stockfly.databinding.FragmentSummaryBinding
import ru.yandex.stockfly.other.setArgument
import ru.yandex.stockfly.ui.WebViewFragmentOpener


@AndroidEntryPoint
class SummaryFragment : BaseViewModelFragment<SummaryViewModel, FragmentSummaryBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_summary"

        @JvmStatic
        fun newInstance(ticker: String): SummaryFragment {
            return SummaryFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            company = viewModel.company
            phoneValue.paintFlags = phoneValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            weburlValue.paintFlags = phoneValue.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.phoneField.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${binding.phoneValue.text}")
            startActivity(intent)
        }
        binding.weburlField.setOnClickListener {
            (requireParentFragment().requireActivity() as WebViewFragmentOpener).openWebViewFragment(
                binding.weburlValue.text.toString()
            )
        }
    }
}
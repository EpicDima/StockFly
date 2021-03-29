package ru.yandex.stockfly.ui.company.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentSummaryBinding

@AndroidEntryPoint
class SummaryFragment : BaseFragment<SummaryViewModel, FragmentSummaryBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_summary"

        @JvmStatic
        fun newInstance(ticker: String): SummaryFragment {
            return SummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(TICKER_KEY, ticker)
                }
            }
        }
    }

    override val viewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            company = viewModel.company
        }
        return binding.root
    }
}
package com.epicdima.stockfly.ui.company.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.base.BaseViewModelFragment
import com.epicdima.stockfly.databinding.FragmentNewsBinding
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.MainRouter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewsFragment : BaseViewModelFragment<NewsViewModel, FragmentNewsBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_news"

        @JvmStatic
        fun newInstance(ticker: String): NewsFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return NewsFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val _viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.v("onCreateView")
        _binding = FragmentNewsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            error = viewModel.error
        }
        setupList()
        return binding.root
    }

    private fun setupList() {
        val adapter = NewsAdapter { url ->
            (requireParentFragment().requireActivity() as MainRouter.WebViewFragmentOpener)
                .openWebViewFragment(url)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        viewModel.news.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.empty = it.isEmpty()
        }
    }
}
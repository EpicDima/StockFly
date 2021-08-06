package com.epicdima.stockfly.ui.company.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBarWidget.root.isVisible = it
            checkVisibility()
        }
        viewModel.error.observe(viewLifecycleOwner) {
            binding.errorWidget.root.isVisible = it
            checkVisibility()
        }
        setupList()
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
            setHasFixedSize(true)
        }
        viewModel.news.observe(viewLifecycleOwner) {
            adapter.submitNewsList(it)
            checkVisibility()
        }
    }

    private fun checkVisibility() {
        binding.recyclerView.isVisible =
            !(viewModel.loading.value == true || viewModel.error.value == true || viewModel.news.value?.isEmpty() == true)
        binding.emptyTextview.isVisible =
            viewModel.news.value?.isEmpty() == true && viewModel.loading.value != true && viewModel.error.value != true
    }
}
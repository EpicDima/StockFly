package com.epicdima.stockfly.ui.company.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.databinding.FragmentNewsBinding
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.setArgument
import com.epicdima.stockfly.ui.MainRouter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : ViewModelFragment<NewsViewModel, FragmentNewsBinding>() {

    companion object {
        const val TICKER_KEY = "ticker_news"

        @JvmStatic
        fun newInstance(ticker: String): NewsFragment {
            Timber.i("newInstance with ticker %s", ticker)
            return NewsFragment().setArgument(TICKER_KEY, ticker)
        }
    }

    override val viewModel: NewsViewModel by viewModels()

    @Inject
    lateinit var formatter: Formatter

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewsBinding {
        return FragmentNewsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.progressBarWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                binding.errorWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        setupList()
    }

    private fun setupList() {
        val adapter = NewsAdapter(formatter) { url ->
            (requireParentFragment().requireActivity() as MainRouter.WebViewFragmentOpener)
                .openWebViewFragment(url)
        }
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            setHasFixedSize(true)
        }
        viewModel.news
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach {
                adapter.submitNewsList(it)
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun checkVisibility() {
        binding.recyclerView.isVisible =
            !(viewModel.loading.value || viewModel.error.value || viewModel.news.value.isEmpty())
        binding.emptyTextview.isVisible =
            viewModel.news.value.isEmpty() == true && viewModel.loading.value != true && viewModel.error.value != true
    }
}
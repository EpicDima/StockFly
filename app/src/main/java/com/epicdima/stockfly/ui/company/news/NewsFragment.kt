package com.epicdima.stockfly.ui.company.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.epicdima.stockfly.R
import com.epicdima.stockfly.base.ViewModelFragment
import com.epicdima.stockfly.databinding.FragmentNewsBinding
import com.epicdima.stockfly.other.CustomTabsProvider
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.setArgument
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
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

    override fun getLayoutId(): Int = R.layout.fragment_news

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean) =
        FragmentNewsBinding.inflate(inflater, container, attachToParent)

    override fun bind(view: View) = FragmentNewsBinding.bind(view)

    @Inject
    lateinit var customTabsProvider: CustomTabsProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.v("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        viewModel.loading
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.progressBarWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        viewModel.error
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach {
                binding.errorWidget.root.isVisible = it
                checkVisibility()
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
        setupList()
    }

    private fun setupList() {
        val adapter = NewsAdapter(formatter) { customTabsProvider.launchUrl(it) }

        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
            setHasFixedSize(true)
        }
        viewModel.news
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onEach { adapter.submitNewsList(it) }
            .onEach { list -> customTabsProvider.mayLaunchManyUrls(list.map { it.url }.toList()) }
            .flowOn(Dispatchers.Default)
            .onEach { checkVisibility() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun checkVisibility() {
        binding.recyclerView.isVisible =
            !(viewModel.loading.value || viewModel.error.value || viewModel.news.value.isEmpty())
        binding.emptyTextview.isVisible =
            viewModel.news.value.isEmpty() == true && viewModel.loading.value != true && viewModel.error.value != true
    }
}
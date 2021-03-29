package ru.yandex.stockfly.ui.company.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.yandex.stockfly.base.BaseFragment
import ru.yandex.stockfly.databinding.FragmentNewsBinding

@AndroidEntryPoint
class NewsFragment : BaseFragment<NewsViewModel, FragmentNewsBinding>() {
    companion object {
        const val TICKER_KEY = "ticker_news"

        @JvmStatic
        fun newInstance(ticker: String): NewsFragment {
            return NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(TICKER_KEY, ticker)
                }
            }
        }
    }

    override val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            loading = viewModel.loading
            error = viewModel.error
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = NewsAdapter { url ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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
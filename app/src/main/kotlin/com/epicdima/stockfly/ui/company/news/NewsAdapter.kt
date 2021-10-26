package com.epicdima.stockfly.ui.company.news

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.load
import coil.request.Disposable
import com.epicdima.stockfly.R
import com.epicdima.stockfly.databinding.ItemNewsItemBinding
import com.epicdima.stockfly.model.NewsItem
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.createUri
import com.epicdima.stockfly.other.getColor
import java.text.DateFormat

class NewsAdapter(
    private val formatter: Formatter,
    private val clickListener: OnNewsItemClickListener
) : ListAdapter<NewsViewHolderItem, NewsAdapter.NewsItemViewHolder>(DIFF_CALLBACK) {

    fun submitNewsList(list: List<NewsItem>) {
        super.submitList(list.mapIndexed { index, newsItem ->
            NewsViewHolderItem(
                index,
                newsItem,
                formatter
            )
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsItemBinding.inflate(inflater, parent, false)
        return NewsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onViewRecycled(holder: NewsItemViewHolder) {
        holder.unbind()
    }


    class NewsItemViewHolder(private val binding: ItemNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var imageDisposable: Disposable? = null

        fun bind(
            newsItem: NewsViewHolderItem,
            clickListener: OnNewsItemClickListener
        ) {
            binding.apply {
                root.setCardBackgroundColor(getColor(newsItem.rootCardBackgroundColor))
                headline.text = newsItem.headline
                summary.text = newsItem.summary
                summary.isVisible = newsItem.summaryIsVisible
                source.text = newsItem.source
                datetime.text = newsItem.datetime
                setImage(imageView, newsItem)
                root.setOnClickListener {
                    clickListener.onClick(newsItem.url)
                }
            }
        }

        private fun setImage(imageView: ImageView, newsItem: NewsViewHolderItem) {
            imageView.apply {
                isVisible = false
                setImageDrawable(null)
                if (newsItem.imageUrl.isNotBlank()) {
                    imageDisposable = load(createUri(newsItem.imageUrl)) {
                        target {
                            setImageDrawable(it)
                            isVisible = true
                        }
                    }
                }
            }
        }

        fun unbind() {
            imageDisposable?.dispose()
            binding.imageView.clear()
            binding.root.setOnClickListener(null)
        }
    }


    fun interface OnNewsItemClickListener {
        fun onClick(url: String)
    }
}


class NewsViewHolderItem(
    position: Int,
    newsItem: NewsItem,
    formatter: Formatter
) {
    val id = newsItem.id
    val url = newsItem.url
    val imageUrl = newsItem.imageUrl
    val headline = newsItem.headline
    val summary = newsItem.summary
    val summaryIsVisible = summary.isNotEmpty()
    val source = newsItem.source
    val datetime: String = DateFormat
        .getDateInstance(DateFormat.MEDIUM, formatter.currentLocale)
        .format(newsItem.datetime * 1000L)

    val rootCardBackgroundColor = if (position % 2 == 0) {
        R.color.light
    } else {
        R.color.white
    }
}


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsViewHolderItem>() {

    override fun areItemsTheSame(
        oldItem: NewsViewHolderItem,
        newItem: NewsViewHolderItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NewsViewHolderItem,
        newItem: NewsViewHolderItem
    ): Boolean {
        return oldItem.url == newItem.url
                && oldItem.imageUrl == newItem.imageUrl
                && oldItem.headline == newItem.headline
                && oldItem.summary == newItem.summary
                && oldItem.source == newItem.source
                && oldItem.datetime == newItem.datetime
    }
}
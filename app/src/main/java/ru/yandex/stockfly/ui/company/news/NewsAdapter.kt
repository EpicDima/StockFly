package ru.yandex.stockfly.ui.company.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.stockfly.base.BaseDiffUtilCallback
import ru.yandex.stockfly.databinding.ItemNewsItemBinding
import ru.yandex.stockfly.model.NewsItem

class NewsAdapter(
    private val clickListener: OnNewsItemClickListener
) : ListAdapter<NewsItem, NewsAdapter.NewsItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsItemBinding.inflate(inflater, parent, false)
        return NewsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        holder.bind(getItem(position), position, clickListener)
    }


    class NewsItemViewHolder(private val binding: ItemNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(newsItem: NewsItem, position: Int, clickListener: OnNewsItemClickListener) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(newsItem.url)
                }
                this.newsItem = newsItem
                this.position = position
                executePendingBindings()
            }
        }
    }


    fun interface OnNewsItemClickListener {
        fun onClick(url: String)
    }
}

private val DIFF_CALLBACK = object : BaseDiffUtilCallback<NewsItem>() {
    override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
        return oldItem.id == newItem.id
    }
}
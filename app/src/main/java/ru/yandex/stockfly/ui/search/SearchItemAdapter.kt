package ru.yandex.stockfly.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.stockfly.base.AsyncListAdapter
import ru.yandex.stockfly.base.BaseDiffUtilCallback
import ru.yandex.stockfly.databinding.ItemSearchBinding
import ru.yandex.stockfly.model.SearchItem

class SearchItemAdapter(
    private val clickListener: OnSearchItemClickListener
) : AsyncListAdapter<SearchItem, SearchItemAdapter.SearchItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)
        return SearchItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        holder.bind(getItem(position), position, clickListener)
    }


    class SearchItemViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchItem: SearchItem, position: Int, clickListener: OnSearchItemClickListener) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(searchItem.ticker)
                }
                this.searchItem = searchItem
                this.position = position
                executePendingBindings()
            }
        }
    }


    fun interface OnSearchItemClickListener {
        fun onClick(ticker: String)
    }
}

private val DIFF_CALLBACK = object : BaseDiffUtilCallback<SearchItem>() {
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
        return oldItem.ticker == newItem.ticker
    }
}
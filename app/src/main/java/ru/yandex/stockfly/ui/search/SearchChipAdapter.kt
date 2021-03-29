package ru.yandex.stockfly.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.stockfly.base.BaseDiffUtilCallback
import ru.yandex.stockfly.databinding.ItemSearchChipBinding


class SearchChipAdapter(
    private val clickListener: OnSearchChipClickListener
) : ListAdapter<String, SearchChipAdapter.SearchChipViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchChipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchChipBinding.inflate(inflater, parent, false)
        return SearchChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchChipViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


    class SearchChipViewHolder(private val binding: ItemSearchChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: String, clickListener: OnSearchChipClickListener) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(request)
                }
                this.request = request
                executePendingBindings()
            }
        }
    }


    fun interface OnSearchChipClickListener {
        fun onClick(request: String)
    }
}

private val DIFF_CALLBACK = BaseDiffUtilCallback<String>()
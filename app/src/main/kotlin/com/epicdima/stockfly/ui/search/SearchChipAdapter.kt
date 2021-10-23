package com.epicdima.stockfly.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.epicdima.stockfly.databinding.ItemSearchChipBinding

class SearchChipAdapter(
    private val clickListener: OnSearchChipClickListener,
    private val longClickListener: OnSearchChipLongClickListener? = null,
) : ListAdapter<String, SearchChipAdapter.SearchChipViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchChipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchChipBinding.inflate(inflater, parent, false)
        return SearchChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchChipViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, longClickListener)
    }


    class SearchChipViewHolder(private val binding: ItemSearchChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            request: String,
            clickListener: OnSearchChipClickListener,
            longClickListener: OnSearchChipLongClickListener?
        ) {
            binding.apply {
                root.setOnClickListener {
                    clickListener.onClick(request)
                }
                if (longClickListener != null) {
                    root.setOnLongClickListener {
                        longClickListener.onLongClick(root, request)
                        true
                    }
                } else {
                    root.setOnLongClickListener(null)
                    root.isLongClickable = false
                }
                this.requestTextview.text = request
            }
        }
    }


    fun interface OnSearchChipClickListener {
        fun onClick(request: String)
    }


    fun interface OnSearchChipLongClickListener {
        fun onLongClick(view: View, request: String)
    }
}


private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
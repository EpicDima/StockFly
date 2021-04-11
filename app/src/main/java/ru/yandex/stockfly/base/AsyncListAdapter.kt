package ru.yandex.stockfly.base

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AsyncListAdapter<T, VH : RecyclerView.ViewHolder>(
    private val diffUtilCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {

    private val asyncListDiffer: AsyncListDiffer<T> = createAsyncListDiffer()

    private fun createAsyncListDiffer(): AsyncListDiffer<T> {
        return AsyncListDiffer(this, diffUtilCallback)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun submitList(list: List<T>) {
        asyncListDiffer.submitList(list)
    }

    protected fun getItem(position: Int): T {
        return asyncListDiffer.currentList[position]
    }
}
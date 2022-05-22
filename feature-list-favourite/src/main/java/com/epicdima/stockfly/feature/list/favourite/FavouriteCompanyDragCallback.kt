package com.epicdima.stockfly.feature.list.favourite

import android.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class FavouriteCompanyDragCallback(
    private val onMoveListener: (from: Int, to: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        Timber.v("onMove")

        (viewHolder.itemView.tag as? PopupMenu)?.dismiss()

        onMoveListener(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}
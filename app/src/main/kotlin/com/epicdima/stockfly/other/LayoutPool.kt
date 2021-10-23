package com.epicdima.stockfly.other

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

private typealias LayoutKey = Pair<Int, ViewGroup?>
private typealias LayoutQueue = ArrayDeque<View>

class LayoutPool(context: Context) {

    private val asyncLayoutInflater = AsyncLayoutInflater(context)

    private val layoutMap: MutableMap<LayoutKey, LayoutQueue> = mutableMapOf()

    fun getLayout(@LayoutRes id: Int, parent: ViewGroup? = null): View? {
        val key = LayoutKey(id, parent)
        val queue = layoutMap[key]

        val view = queue?.removeFirstOrNull()

        createLayout(key, queue)

        Timber.i("getLayout %s by key %s", view, key)
        return view
    }

    private fun createLayout(key: LayoutKey, queue: LayoutQueue?) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(400L) // задержка, чтобы не нагружать железо во время отображения данных

            if (queue == null) {
                layoutMap[key] = ArrayDeque(1)
            }

            if (queue.isNullOrEmpty()) {
                inflate(key)
            }
        }
    }

    private fun inflate(key: LayoutKey) {
        asyncLayoutInflater.inflate(key.first, key.second) { layoutView, _, _ ->
            Timber.i("async inflate %s", key)
            layoutMap[key]?.addLast(layoutView)
        }
    }
}
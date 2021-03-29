package ru.yandex.stockfly.other

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import kotlinx.coroutines.*

fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}

fun Float.spToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

fun Resources.getDimensionInSp(id: Int): Float {
    return getDimension(id) / displayMetrics.scaledDensity
}


private const val TIMEOUT_MILLIS = 10_000L

fun CoroutineScope.timeout(onTimeout: () -> Unit): Job {
    return launch {
        try {
            withTimeout(TIMEOUT_MILLIS) {
                awaitCancellation()
            }
        } catch (e: TimeoutCancellationException) {
            onTimeout()
        }
    }
}
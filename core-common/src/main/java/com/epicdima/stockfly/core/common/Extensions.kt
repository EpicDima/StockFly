package com.epicdima.stockfly.core.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import timber.log.Timber

fun <T : Fragment> T.setArgument(key: String, value: String): T {
    arguments = Bundle().apply {
        putString(key, value)
    }
    return this
}

private const val TIMEOUT_MILLIS = 10_000L

internal fun CoroutineScope.timeout(onTimeout: () -> Unit): Job {
    return launch {
        try {
            withTimeout(TIMEOUT_MILLIS) {
                awaitCancellation()
            }
        } catch (e: TimeoutCancellationException) {
            Timber.w(e)
            onTimeout()
        }
    }
}
package com.epicdima.stockfly.other

import android.content.SharedPreferences
import com.epicdima.stockfly.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

private const val REFRESH_INTERVAL = 28800 * 1000L // 8 часов (3 раза в сутки)
private const val ERROR_INTERVAL = 120 * 1000L // 2 минуты

class Refresher(
    private val repository: Repository,
    private val preferences: SharedPreferences
) {

    fun refresh() {
        Timber.i("refresh")
        repeatOnError(this::refreshCompanies)
    }

    private suspend fun refreshCompanies() {
        val time = preferences.getLastRefreshTime()
        val currentTime = System.currentTimeMillis()
        if (currentTime - time > REFRESH_INTERVAL) {
            repository.refreshCompanies()
            preferences.setLastRefreshTime(System.currentTimeMillis())
        }
    }

    private fun repeatOnError(function: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                try {
                    function()
                    break
                } catch (e: Exception) {
                    Timber.w(e, "repeatOnError")
                    delay(ERROR_INTERVAL)
                }
            }
        }
    }
}
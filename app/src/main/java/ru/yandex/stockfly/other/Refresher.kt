package ru.yandex.stockfly.other

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.yandex.stockfly.repository.Repository

private const val REFRESH_INTERVAL = 28800 * 1000L // 8 часов (3 раза в сутки)
private const val ERROR_INTERVAL = 120 * 1000L // 2 минуты

class Refresher(
    private val repository: Repository,
    private val preferences: SharedPreferences
) {

    fun refresh() {
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
        GlobalScope.launch {
            while (true) {
                try {
                    function()
                    break
                } catch (e: Exception) {
                    Log.w(this@Refresher::class.simpleName, "repeatOnError", e)
                    delay(ERROR_INTERVAL)
                }
            }
        }
    }
}
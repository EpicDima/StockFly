package ru.yandex.stockfly.other

import android.content.SharedPreferences
import androidx.core.content.edit


private const val SEARHED_KEY = "searched"
private const val LAST_REFRESH_KEY = "last_refresh"

fun SharedPreferences.getSearched(): String? {
    return getString(SEARHED_KEY, null)
}

fun SharedPreferences.setSearched(searched: String) {
    edit {
        putString(SEARHED_KEY, searched)
    }
}

fun SharedPreferences.getLastRefreshTime(): Long {
    return getLong(LAST_REFRESH_KEY, 0L)
}

fun SharedPreferences.setLastRefreshTime(time: Long) {
    edit {
        putLong(LAST_REFRESH_KEY, time)
    }
}
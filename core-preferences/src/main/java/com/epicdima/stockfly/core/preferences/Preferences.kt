package com.epicdima.stockfly.core.preferences

import android.content.SharedPreferences

private const val SEARHED_KEY = "searched"

fun SharedPreferences.getSearched(): String? {
    return getString(SEARHED_KEY, null)
}

fun SharedPreferences.setSearched(searched: String) {
    edit().apply {
        putString(SEARHED_KEY, searched)
    }.apply()
}
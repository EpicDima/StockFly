package com.epicdima.stockfly.other

import android.content.SharedPreferences
import androidx.core.content.edit

private const val SEARHED_KEY = "searched"

fun SharedPreferences.getSearched(): String? {
    return getString(SEARHED_KEY, null)
}

fun SharedPreferences.setSearched(searched: String) {
    edit {
        putString(SEARHED_KEY, searched)
    }
}
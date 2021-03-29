package ru.yandex.stockfly.other

import android.content.SharedPreferences
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter


const val APP_PREFERENCES = "app_preferences"

const val SEARHED_KEY = "searched"
const val MAX_SEARCHED_REQUESTS = 50


fun SharedPreferences.getSearched(adapter: JsonAdapter<List<String>>): List<String> {
    return adapter.fromJson(getString(SEARHED_KEY, "[]") ?: "[]") ?: emptyList()
}

fun SharedPreferences.setSearched(searched: List<String>, adapter: JsonAdapter<List<String>>) {
    edit {
        putString(SEARHED_KEY, adapter.toJson(searched))
    }
}
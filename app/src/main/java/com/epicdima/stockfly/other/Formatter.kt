package com.epicdima.stockfly.other

import android.content.Context
import android.os.Build
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Formatter(context: Context) {

    private val stringDateFormatMap = mutableMapOf<String, SimpleDateFormat>()

    val currentLocale: Locale = context.resources.configuration.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            it.locales[0]
        } else {
            @Suppress("DEPRECATION")
            it.locale
        }
    }

    private val numberFormat: NumberFormat = NumberFormat.getNumberInstance(currentLocale)

    private val percentFormat: NumberFormat = NumberFormat.getPercentInstance(currentLocale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    fun getSimpleDateFormat(pattern: String): SimpleDateFormat {
        return stringDateFormatMap.getOrPut(pattern) { SimpleDateFormat(pattern, currentLocale) }
    }

    fun numberFormat(obj: Any): String {
        return numberFormat.format(obj)
    }

    fun percentFormat(obj: Any): String {
        return percentFormat.format(obj)
    }
}
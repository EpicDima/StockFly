package com.epicdima.stockfly.other

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Formatter {

    private var currentLocale: Locale = Locale.getDefault()

    private val stringDateFormatMap = mutableMapOf<String, SimpleDateFormat>()

    private lateinit var numberFormat: NumberFormat
    private lateinit var percentFormat: NumberFormat

    init {
        resetNumberFormat()
        resetPercentFormat()
    }

    fun updateLocale(locale: Locale) {
        if (locale != currentLocale) {
            currentLocale = locale
            resetStringDateFormatMap()
            resetNumberFormat()
            resetPercentFormat()
        }
    }

    fun getCurrentLocale(): Locale {
        return currentLocale
    }

    private fun resetStringDateFormatMap() {
        stringDateFormatMap.clear()
    }

    private fun resetNumberFormat() {
        numberFormat = NumberFormat.getNumberInstance(currentLocale)
    }

    private fun resetPercentFormat() {
        percentFormat = NumberFormat.getPercentInstance(currentLocale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    fun getSimpleDateFormat(pattern: String): SimpleDateFormat {
        return stringDateFormatMap.getOrPut(pattern) { SimpleDateFormat(pattern, currentLocale) }
    }

    fun getNumberFormat(): NumberFormat {
        return numberFormat
    }

    fun getPercentFormat(): NumberFormat {
        return percentFormat
    }
}

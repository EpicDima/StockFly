package com.epicdima.stockfly.core.network

import java.text.SimpleDateFormat
import java.util.*

private val FORMAT_DATE_API = SimpleDateFormat("yyyy-MM-dd", Locale.US)
private const val DAYS_IN_WEEK = 7

internal fun getStringDateNow(): String {
    return FORMAT_DATE_API.format(Calendar.getInstance().time)
}

internal fun getStringDateWeekEarlier(): String {
    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_YEAR, -DAYS_IN_WEEK)
    return FORMAT_DATE_API.format(date.time)
}
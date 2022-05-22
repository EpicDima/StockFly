package com.epicdima.stockfly.core.formatter

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

val FORMAT_DATE_API = SimpleDateFormat("yyyy-MM-dd", Locale.US)

const val PATTERN_WITH_MINUTES = "HH:mm dd MMM yyyy"
const val PATTERN_DATE = "dd MMM yyyy"
const val PATTERN_DATE_WITHOUT_DAY = "LLLL yyyy"

// Locale.US потому что API отдаёт значения в долларах
val FORMAT_PRICE: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 5
    isGroupingUsed = false
}

val FORMAT_CHANGE: NumberFormat = (FORMAT_PRICE.clone() as DecimalFormat).apply {
    positivePrefix = "+${currency!!.symbol}"
}
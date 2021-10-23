package com.epicdima.stockfly.other

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

const val PATTERN_DATE_API = "yyyy-MM-dd"
val FORMAT_DATE_API = SimpleDateFormat(PATTERN_DATE_API, Locale.US)
private const val WEEK = 7

fun getStringDateNow(): String {
    return FORMAT_DATE_API.format(Calendar.getInstance().time)
}

fun getStringDateWeekEarlier(): String {
    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_YEAR, -WEEK)
    return FORMAT_DATE_API.format(date.time)
}

private const val PATTERN_WITH_MINUTES = "HH:mm dd MMM yyyy"
private const val PATTERN_DATE = "dd MMM yyyy"
const val PATTERN_DATE_WITHOUT_DAY = "LLLL yyyy"


enum class StockCandleParam(
    val resolution: String,
    private val seconds: Long,
    private val pattern: String
) {
    DAY("5", 86_400, PATTERN_WITH_MINUTES),
    WEEK("15", 604_800, PATTERN_WITH_MINUTES),
    MONTH("60", 2_592_000, PATTERN_WITH_MINUTES),
    SIX_MONTHS("D", 15_768_000, PATTERN_DATE),
    YEAR("D", 31_536_000, PATTERN_DATE),
    ALL_TIME("M", Long.MAX_VALUE, PATTERN_DATE_WITHOUT_DAY);

    fun getTimeInterval(): Pair<Long, Long> {
        val end = System.currentTimeMillis() / 1000L
        if (this == ALL_TIME) {
            return Pair(0, end)
        }
        return Pair(end - seconds, end)
    }

    fun format(formatter: Formatter): SimpleDateFormat {
        return formatter.getSimpleDateFormat(pattern)
    }
}


// Locale.US потому что API отдаёт значения в долларах
val FORMAT_PRICE: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 5
    isGroupingUsed = false
}

val FORMAT_CHANGE: NumberFormat = (FORMAT_PRICE.clone() as DecimalFormat).apply {
    positivePrefix = "+${currency!!.symbol}"
}
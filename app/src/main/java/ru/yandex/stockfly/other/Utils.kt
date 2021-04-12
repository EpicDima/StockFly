package ru.yandex.stockfly.other

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

fun getDateWeek(number: Int): Pair<Long, Long> {
    val start = Calendar.getInstance()
    start.add(Calendar.DAY_OF_YEAR, -WEEK * number)
    val end = Calendar.getInstance()
    end.add(Calendar.DAY_OF_YEAR, -WEEK * (number - 1))
    return Pair(start.time.time / 1000L, end.time.time / 1000L)
}

fun getStringDateWeek(number: Int): Pair<String, String> {
    val (from, to) = getDateWeek(number)
    return Pair(
        FORMAT_DATE_API.format(Date(from * 1000L)),
        FORMAT_DATE_API.format(Date(to * 1000L))
    )
}


private const val PATTERN_WITH_MINUTES = "HH:mm dd MMM yyyy"
private const val PATTERN_DATE = "dd MMM yyyy"
private const val PATTERN_DATE_WITHOUT_DAY = "LLLL yyyy"


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

    val format: SimpleDateFormat
        get() = Formatter.getSimpleDateFormat(pattern)
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

val FORMAT_CHANGE_PERCENT: NumberFormat
    get() = Formatter.getPercentFormat()

val FORMAT_PERIOD_DATE: SimpleDateFormat
    get() = Formatter.getSimpleDateFormat(PATTERN_DATE_WITHOUT_DAY)
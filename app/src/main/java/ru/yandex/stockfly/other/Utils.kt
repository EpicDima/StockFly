package ru.yandex.stockfly.other

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val FORMAT_DATE_FOR_API = SimpleDateFormat("yyyy-MM-dd", Locale.US)
private const val WEEK = 7

fun getStringDateNow(): String {
    return FORMAT_DATE_FOR_API.format(Calendar.getInstance())
}

fun getStringDateWeekEarlier(): String {
    val date = Calendar.getInstance()
    date.add(Calendar.DAY_OF_YEAR, -WEEK)
    return FORMAT_DATE_FOR_API.format(date)
}

private val FORMAT_WITH_MINUTES = SimpleDateFormat("HH:mm dd MMM yyyy", Locale.US)
private val FORMAT_DATE = SimpleDateFormat("dd MMM yyyy", Locale.US)
private val FORMAT_DATE_WITHOUT_DAY = SimpleDateFormat("MMM yyyy", Locale.US)

enum class StockCandleParam(
    val resolution: String,
    private val seconds: Long,
    val format: SimpleDateFormat
) {
    DAY("5", 86_400, FORMAT_WITH_MINUTES),
    WEEK("15", 604_800, FORMAT_WITH_MINUTES),
    MONTH("60", 2_592_000, FORMAT_WITH_MINUTES),
    SIX_MONTHS("D", 15_768_000, FORMAT_DATE),
    YEAR("D", 31_536_000, FORMAT_DATE),
    ALL_TIME("M", Long.MAX_VALUE, FORMAT_DATE_WITHOUT_DAY);

    fun getTimeInterval(): Pair<Long, Long> {
        val end = System.currentTimeMillis() / 1000L
        if (this == ALL_TIME) {
            return Pair(0, end)
        }
        return Pair(end - seconds, end)
    }
}


// Locale.US потому что API отдаёт значения в долларах
val FORMAT_PRICE: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 5
}

val FORMAT_CHANGE: NumberFormat =
    (NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 5
        positivePrefix = "+${currency!!.symbol}"
    }

val FORMAT_CHANGE_PERCENT: NumberFormat = NumberFormat.getPercentInstance(Locale.US).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}


data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
) {
    override fun toString(): String = "($first, $second, $third, $fourth)"
}


fun Context.color(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, this.theme)
}

fun Context.drawable(@DrawableRes id: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, id, this.theme)!!
}

fun TabLayout.Tab.set(textSize: Float, @ColorRes id: Int) {
    view.findViewById<TextView>(android.R.id.text1).apply {
        this.textSize = textSize
        setTextColor(context.color(id))
    }
}
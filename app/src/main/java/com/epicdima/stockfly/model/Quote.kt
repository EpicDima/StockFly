package com.epicdima.stockfly.model

import com.epicdima.stockfly.other.FORMAT_CHANGE
import com.epicdima.stockfly.other.FORMAT_PRICE
import com.epicdima.stockfly.other.Formatter
import kotlin.math.absoluteValue

data class Quote(
    val current: Double = 0.0,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val previousClose: Double = 0.0
) {
    private val change: Double
        get() = current - previousClose

    private val changePercent: Double
        get() {
            val result = change.absoluteValue / (current + previousClose)
            return if (result.isNaN()) 0.0 else result
        }

    val currentString: String
        get() = FORMAT_PRICE.format(current)

    val changeString: String
        get() {
            val result = FORMAT_CHANGE.format(change)
            return if ("+0" == result) "0" else result
        }

    fun changePercentString(formatter: Formatter): String {
        return formatter.getPercentFormat().format(changePercent)
    }
}

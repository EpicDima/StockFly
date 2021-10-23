package com.epicdima.stockfly.model

import com.epicdima.stockfly.other.FORMAT_DATE_API
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.PATTERN_DATE_WITHOUT_DAY
import java.util.*

data class Recommendation(
    val period: String = "",
    val strongBuy: Int = 0,
    val buy: Int = 0,
    val hold: Int = 0,
    val sell: Int = 0,
    val strongSell: Int = 0,
) {
    private val periodDate: Date
        get() = FORMAT_DATE_API.parse(period) ?: Date(0)

    fun periodFormatted(formatter: Formatter): String {
        return formatter
            .getSimpleDateFormat(PATTERN_DATE_WITHOUT_DAY)
            .format(periodDate).replaceFirstChar { it.titlecase() }
    }

    val sum: Int
        get() = strongBuy + buy + hold + sell + strongSell
}

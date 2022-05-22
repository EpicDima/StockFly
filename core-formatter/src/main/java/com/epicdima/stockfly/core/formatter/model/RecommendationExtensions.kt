package com.epicdima.stockfly.core.formatter.model

import com.epicdima.stockfly.core.formatter.FORMAT_DATE_API
import com.epicdima.stockfly.core.formatter.Formatter
import com.epicdima.stockfly.core.formatter.PATTERN_DATE_WITHOUT_DAY
import com.epicdima.stockfly.core.model.Recommendation
import java.util.*

val Recommendation.periodDate: Date
    get() = FORMAT_DATE_API.parse(period) ?: Date(0)

fun Recommendation.periodFormatted(formatter: Formatter): String {
    return formatter
        .getSimpleDateFormat(PATTERN_DATE_WITHOUT_DAY)
        .format(periodDate).replaceFirstChar { it.titlecase() }
}
package com.epicdima.stockfly.core.formatter.model

import com.epicdima.stockfly.core.formatter.Formatter
import com.epicdima.stockfly.core.formatter.PATTERN_DATE
import com.epicdima.stockfly.core.formatter.PATTERN_DATE_WITHOUT_DAY
import com.epicdima.stockfly.core.formatter.PATTERN_WITH_MINUTES
import com.epicdima.stockfly.core.model.StockCandleParam
import java.text.SimpleDateFormat

fun StockCandleParam.format(formatter: Formatter): SimpleDateFormat {
    return when (this) {
        StockCandleParam.DAY, StockCandleParam.WEEK, StockCandleParam.MONTH -> formatter.getSimpleDateFormat(
            PATTERN_WITH_MINUTES
        )
        StockCandleParam.SIX_MONTHS, StockCandleParam.YEAR -> formatter.getSimpleDateFormat(
            PATTERN_DATE
        )
        StockCandleParam.ALL_TIME -> return formatter.getSimpleDateFormat(PATTERN_DATE_WITHOUT_DAY)
    }
}
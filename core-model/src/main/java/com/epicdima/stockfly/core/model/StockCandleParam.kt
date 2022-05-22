package com.epicdima.stockfly.core.model

enum class StockCandleParam(
    val resolution: String,
    private val seconds: Long,
) {
    DAY("5", 86_400),
    WEEK("15", 604_800),
    MONTH("60", 2_592_000),
    SIX_MONTHS("D", 15_768_000),
    YEAR("D", 31_536_000),
    ALL_TIME("M", Long.MAX_VALUE);

    fun getTimeInterval(): Pair<Long, Long> {
        val end = System.currentTimeMillis() / 1000L
        if (this == ALL_TIME) {
            return Pair(0, end)
        }
        return Pair(end - seconds, end)
    }
}
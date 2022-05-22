package com.epicdima.stockfly.core.model

import kotlin.math.absoluteValue

data class Quote(
    val current: Double = 0.0,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val previousClose: Double = 0.0
) {
    val change: Double = current - previousClose

    val changePercent: Double
        get() {
            val result = change.absoluteValue / (current + previousClose)
            return if (result.isNaN()) 0.0 else result
        }
}

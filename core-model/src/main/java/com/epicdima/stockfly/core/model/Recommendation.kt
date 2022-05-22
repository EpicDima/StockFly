package com.epicdima.stockfly.core.model

data class Recommendation(
    val period: String = "",
    val strongBuy: Int = 0,
    val buy: Int = 0,
    val hold: Int = 0,
    val sell: Int = 0,
    val strongSell: Int = 0,
) {

    val sum: Int = strongBuy + buy + hold + sell + strongSell
}

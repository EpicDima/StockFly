package com.epicdima.stockfly.core.buildconfig

class BuildConfigContainer(
    val isDebug: Boolean,
    val versionName: String,
    val versionCode: Int,
    val apiKey: String,
    val detailedSearch: Boolean
)
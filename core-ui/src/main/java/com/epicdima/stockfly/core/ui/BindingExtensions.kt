package com.epicdima.stockfly.core.ui

import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.viewbinding.ViewBinding

fun ViewBinding.getColor(@ColorRes colorId: Int): Int {
    return ResourcesCompat.getColor(
        root.context.resources,
        colorId,
        root.context.theme
    )
}
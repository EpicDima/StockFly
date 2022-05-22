package com.epicdima.stockfly.core.ui

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat

fun Context.color(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, this.theme)
}

fun Context.drawable(@DrawableRes id: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, id, this.theme)!!
}
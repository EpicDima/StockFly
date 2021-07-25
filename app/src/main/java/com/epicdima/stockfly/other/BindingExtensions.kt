package com.epicdima.stockfly.other

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.viewbinding.ViewBinding

fun ViewBinding.getColor(@ColorRes colorId: Int): Int {
    return ResourcesCompat.getColor(
        root.context.resources,
        colorId,
        root.context.theme
    )
}

fun ViewBinding.getDrawable(@DrawableRes drawableId: Int): Drawable? {
    return ResourcesCompat.getDrawable(
        root.context.resources,
        drawableId,
        root.context.theme
    )
}
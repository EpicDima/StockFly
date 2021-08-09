package com.epicdima.stockfly.other

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.viewbinding.ViewBinding
import coil.load

fun loadImageWithGoneOnError(imageView: ImageView, imageUrl: String?) {
    if (imageUrl.isNullOrEmpty()) {
        imageView.visibility = View.GONE
        return
    }

    imageView.load(createUri(imageUrl)) {
        target(onStart = {
            imageView.setImageDrawable(null)
        }, onSuccess = {
            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(it)
        }, onError = {
            imageView.visibility = View.GONE
        })
    }
}

fun ViewBinding.getColor(@ColorRes colorId: Int): Int {
    return ResourcesCompat.getColor(
        root.context.resources,
        colorId,
        root.context.theme
    )
}
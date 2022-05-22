package com.epicdima.stockfly.feature.details.summary

import android.view.View
import android.widget.ImageView
import coil.load
import com.epicdima.stockfly.core.utils.createUri

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
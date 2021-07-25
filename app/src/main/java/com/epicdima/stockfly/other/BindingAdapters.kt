package com.epicdima.stockfly.other

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

private fun bindImageWithGoneOnError(imageView: ImageView, imageUrl: String?) {
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


@BindingAdapter("newsItemImageUrl")
fun bindNewsImageInNewsItem(imageView: ImageView, imageUrl: String?) {
    bindImageWithGoneOnError(imageView, imageUrl)
}


@BindingAdapter("companySummaryLogoUrl")
fun bindCompanyLogoInCompanySummary(imageView: ImageView, logoUrl: String?) {
    bindImageWithGoneOnError(imageView, logoUrl)
}
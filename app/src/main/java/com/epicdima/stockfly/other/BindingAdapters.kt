package com.epicdima.stockfly.other

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load

private fun bindImageWithGoneOnError(imageView: ImageView, imageUrl: String?) {
    if (imageUrl == null) {
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

private fun createUri(url: String): Uri {
    return url.toUri().buildUpon().scheme("https").build()
}


@BindingAdapter("newsItemImageUrl")
fun bindNewsImageInNewsItem(imageView: ImageView, imageUrl: String?) {
    bindImageWithGoneOnError(imageView, imageUrl)
}


@BindingAdapter("companySummaryLogoUrl")
fun bindCompanyLogoInCompanySummary(imageView: ImageView, logoUrl: String?) {
    bindImageWithGoneOnError(imageView, logoUrl)
}


@BindingAdapter("companyItemLogoUrl", "backgroundColor", requireAll = true)
fun bindCompanyLogoAndBackgroundColorInCompanyItem(
    imageView: ImageView,
    logoUrl: String?,
    backgroundColor: Int
) {
    imageView.setBackgroundColor(backgroundColor)
    if (logoUrl == null) {
        return
    }
    imageView.apply {
        load(createUri(logoUrl)) {
            target {
                background = null
                setImageDrawable(it)
            }
        }
    }
}
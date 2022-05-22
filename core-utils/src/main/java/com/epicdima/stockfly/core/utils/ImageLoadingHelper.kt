package com.epicdima.stockfly.core.utils

import android.content.Context
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult

fun createUri(url: String): Uri {
    return Uri.parse(url).buildUpon().scheme("https").build()
}

fun Context.createImageRequest(url: String): ImageRequest {
    return ImageRequest.Builder(this)
        .memoryCacheKey(url)
        .data(createUri(url))
        .build()
}

suspend fun Context.executeImageRequest(url: String): ImageResult {
    return imageLoader.execute(createImageRequest(url))
}
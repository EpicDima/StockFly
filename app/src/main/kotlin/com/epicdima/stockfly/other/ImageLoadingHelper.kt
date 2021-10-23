package com.epicdima.stockfly.other

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult

fun createUri(url: String): Uri {
    return url.toUri().buildUpon().scheme("https").build()
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
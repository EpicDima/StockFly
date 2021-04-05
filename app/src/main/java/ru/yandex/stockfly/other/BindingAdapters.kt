package ru.yandex.stockfly.other

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import coil.request.CachePolicy

@BindingAdapter("imageUrl")
fun bindImageWithGoneOnError(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            target(onStart = {
                imgView.setImageDrawable(null)
            }, onSuccess = {
                imgView.setImageDrawable(it)
            }, onError = {
                imgView.visibility = View.GONE
            })
        }
    }
}

// Не работает почему-то, когда два значения в одной аннотации, даже при изменении requireAll
@BindingAdapter("newsItemImageUrl")
fun bindImageWithGoneOnError2(imgView: ImageView, imgUrl: String?) {
    bindImageWithGoneOnError(imgView, imgUrl)
}

@BindingAdapter("companyItemImageUrl")
fun bindImageWithoutBackgroundOnSuccess(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            diskCachePolicy(CachePolicy.ENABLED)
            target {
                imgView.background = null
                imgView.setImageDrawable(it)
            }
        }
    }
}
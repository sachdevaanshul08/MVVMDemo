package com.demo.ui.adapters.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.demo.R


@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view.getContext())
        .load(imageUrl).placeholder(R.drawable.ic_launcher_foreground)
        .apply(RequestOptions.centerInsideTransform())
        .into(view)
}


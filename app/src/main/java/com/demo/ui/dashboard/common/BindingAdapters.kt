package com.demo.ui.dashboard.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.demo.R

class BindingAdapters {
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String) {
            Glide.with(view.getContext())
                .load(imageUrl).placeholder(R.drawable.ic_launcher_background)
                .apply(RequestOptions.centerCropTransform())
                .into(view)
        }
    }
}
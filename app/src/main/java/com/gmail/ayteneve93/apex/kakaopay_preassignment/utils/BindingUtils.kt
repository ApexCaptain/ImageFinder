package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingUtils {


    @JvmStatic
    @BindingAdapter("profileImage")
    fun loadImage(view : ImageView, imageUrl : String) {
        Glide.with(view.context)
            .load(imageUrl).apply(RequestOptions.circleCropTransform())
            .into(view)
    }


}
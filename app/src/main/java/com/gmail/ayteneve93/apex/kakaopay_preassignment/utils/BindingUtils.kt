package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingUtils {


    @JvmStatic
    @BindingAdapter("thumbnail")
    fun loadImage(view : ImageView, imageUrl : String) {
        Glide.with(view.context)
            .load(imageUrl).apply(RequestOptions.circleCropTransform())
            .into(view)
    }


    @JvmStatic
    @BindingAdapter("layout_constraintGuide_begin")
    fun setLayoutConstraintGuideBegin(guideline : Guideline, percent : Float) {
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent
        guideline.layoutParams = params
    }


}
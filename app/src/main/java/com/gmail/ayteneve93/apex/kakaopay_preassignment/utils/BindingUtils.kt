package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R

object BindingUtils {


    @JvmStatic
    @BindingAdapter("thumbnail")
    fun loadThumbnail(view : ImageView, imageUrl : String) {
        Glide.with(view.context)
            .load(imageUrl).apply(RequestOptions.circleCropTransform())
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("mainImage")
    fun loadMainImage(view : ImageView, imageUrl : String) {
        Glide.with(view.context)
            .load(imageUrl)
            .error(R.drawable.ic_no_search_result)
            .placeholder(CircularProgressDrawable(view.context).apply {
                strokeWidth = 10f
                centerRadius = 100f
                start()
            })
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("layout_constraintGuide_begin")
    fun setLayoutConstraintGuideBegin(guideline : Guideline, percent : Float) {
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent
        guideline.layoutParams = params
    }

    @JvmStatic
    @BindingAdapter("setImageDetailDocumentClient")
    fun setImageDetailDocumentClient(view : WebView, client : WebViewClient) {
        view.webViewClient = client
    }

    @JvmStatic
    @BindingAdapter("loadImageDetailDocumentUrl")
    fun loadImageDetailDocumentUrl(view : WebView, url : String) {
        view.loadUrl(url)
    }


}
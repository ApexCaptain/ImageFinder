package com.gmail.ayteneve93.apex.kakaopay_preassignment.utils

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

/**
 * MVVM 디자인 패턴을 구축할 때 사용한 BindingAdapter 메소드를 모아둔
 * Object 입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
object BindingUtils {


    /**
     * ImageView 에 이미지를 삽입할 때 사용하는 메소드입니다.
     * 네트워크 Url 을 받아와서 이미지를 입히며 Glide 를 사용했습니다.
     *
     * @param view 이미지 삽입의 주체가 되는 ImageView 입니다
     * @param imageUrl 삽입될 이미지의 Url 입니다
     */
    @JvmStatic
    @BindingAdapter("thumbnail")
    fun loadThumbnail(view : ImageView, imageUrl : String) {
        Glide.with(view.context)
            .load(imageUrl).apply(RequestOptions.circleCropTransform())
            .into(view)
    }

    /**
     * ImageView 에 이미지를 삽입할 때 사용하는 메소드입니다.
     * 네트워크 Url 을 받아와서 이미지를 입히며 Glide 를 사용했습니다.
     * 추가적으로 이번엔 thumbnail 이 아닌 용량이 큰 메인 이미지 파일을 로드합니다.
     * 따라서 PlaceHolder 를 CircularProgressDrawable 로 달아 이미지가 로드 중임을
     * 표시합니다.
     *
     * @param view 이미지 삽입의 주체가 되는 ImageView 입니다
     * @param imageUrl 삽입될 이미지의 Url 입니다
     */
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

    /**
     * 이미지 Thumbnail 을 표시하는 Recycler View 는 Pinch 제스쳐로
     * 확대/축소를 지원하는데, 이 때 ConstraintLayout 의 Guideline 을 두고
     * GuideLine 의 시작 위치를 percent 로 제어해서 구현합니다.
     *
     * @param guideline 리사이징에 주체가 되는 Guideline 입니다.
     * @param percent Guideline 의 크기입니다. 1.0 일 경우 최대, 0.0 일 경우 최소입니다.
     *
     */
    @JvmStatic
    @BindingAdapter("layout_constraintGuide_begin")
    fun setLayoutConstraintGuideBegin(guideline : Guideline, percent : Float) {
        val params = guideline.layoutParams as ConstraintLayout.LayoutParams
        params.guidePercent = percent
        guideline.layoutParams = params
    }

    /**
     * WebView 에 Client 를 삽입하는 Binding 입니다.
     *
     * @param view 클라이언트 삽입의 주체가 되는 WebView 입니다.
     * @param client 삽입될 클라이언트입니다.
     */
    @JvmStatic
    @BindingAdapter("setImageDetailDocumentClient")
    fun setImageDetailDocumentClient(view : WebView, client : WebViewClient) {
        view.webViewClient = client
    }

    /**
     * WebView 에 Url 로 Document 를 삽입하는 Binding 입니다.
     *
     * @param view 이미지 삽입의 주체가 되는 ImageView 입니다
     * @param url 삽입될 Document 의 Url 입니다.
     */
    @JvmStatic
    @BindingAdapter("loadImageDetailDocumentUrl")
    fun loadImageDetailDocumentUrl(view : WebView, url : String) {
        view.loadUrl(url)
    }


}
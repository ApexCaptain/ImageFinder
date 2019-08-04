package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_detail

import android.app.Application
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageDetailViewModel (
    application: Application,
    imageModel : KakaoImageModel,
    private val mImageOperationController: ImageOperationController
) : BaseViewModel(application) {
    var mKakaoImageModel : KakaoImageModel = imageModel
    val mIsWebViewLoading = ObservableField(true)
    val mImageDetailDocumentClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mIsWebViewLoading.set(false)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            mIsWebViewLoading.set(false)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return false
        }
    }

    lateinit var onInfoButtonClickListener : () -> Unit
    fun boundOnInfoButtonClick() {
        onInfoButtonClickListener()
    }
    fun boundOnDownloadButtonClick() {
        mImageOperationController.addImageModel(mKakaoImageModel)
        mImageOperationController.startDownload()
    }
}





















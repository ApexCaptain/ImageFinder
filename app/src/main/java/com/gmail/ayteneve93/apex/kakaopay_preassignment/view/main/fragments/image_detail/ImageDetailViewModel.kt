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

/**
 * ImageDetailFragment 의 ViewModel 입니다.
 *
 * @property mImageOperationController 이미지 공유/다운로드 제어 객체입니다.
 * @property mKakaoImageModel 이미지 데이터 모델입니다.
 * @property mIsWebViewLoading 웹 뷰가 로딩중인지 표시하는 Observable<Boolean> 객체입니다.
 * @property mImageDetailDocumentClient 웹 뷰에 삽입할 클라이언트 객체입니다.
 * @property onInfoButtonClickListener ImageDetailFragment 에서 설정하는 값으로, 뷰의 Info 버튼이 클릭되었을 때의 처리를 담당합니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
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

    /**
     * 공유 버튼이 눌렸을 경우 공유/다운로드 제어기 객체에 현재 이미지 모델을
     * 전달하고 공유를 시작합니다.
     */
    fun boundOnShareButtonClick() {
        if(!mImageOperationController.mIsOnOperation.get()!!) {
            mImageOperationController.addImageModel(mKakaoImageModel)
            mImageOperationController.startShare()
        }
    }
    lateinit var onInfoButtonClickListener : () -> Unit
    /**
     * 정보 버튼이 눌렸을 경우 Dialog 로 선택한 이미지의 기본 정보가 표시되는데,
     * 이 부분은 Fragmnet 에서 호출해야 되므로 콜백으로 처리했습니다.
     */
    fun boundOnInfoButtonClick() {
        onInfoButtonClickListener()
    }

    /**
     * 다운로드 버튼이 눌렸을 경우 공유/다운로드 제어기 객체에 현재 이미지 모델을
     * 전당하고 다운로드를 시작합니다.
     */
    fun boundOnDownloadButtonClick() {
        if(!mImageOperationController.mIsOnOperation.get()!!) {
            mImageOperationController.addImageModel(mKakaoImageModel)
            mImageOperationController.startDownload()
        }
    }
}





















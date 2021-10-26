package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_detail


import android.app.AlertDialog
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Handler
import androidx.core.content.ContextCompat
import com.gmail.ayteneve93.apex.kakaopay_preassignment.BR

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageDetailBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference

/**
 * 선택한 이미지의 상세한 내용을 보여주는 프래그먼트입니다. 이미지 원본과 이미지가 게시된
 * 웹 페이지를 표시해주는 WebView, 그리고 공유, 정보, 다운로드 3개의 FAB 로 구성되어 있습니다.
 *
 * @property mImageDetailViewModel 이미지 상세정보 프래그먼트의 뷰 모델입니다.
 * @property mImageDetailBroadcastReceiver 이미지 상세정보 프래그먼트에서 사용하는 방송 수신자입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress("SetJavaScriptEnabled")
class ImageDetailFragment(application : Application, imageModel: KakaoImageModel, mImageOperationController: ImageOperationController) : BaseFragment<FragmentImageDetailBinding, ImageDetailViewModel>() {

    private val mImageDetailViewModel : ImageDetailViewModel = ImageDetailViewModel(application, imageModel, mImageOperationController)
    private val mImageDetailBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                    actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                        target ->
                        if(target == MainBroadcastPreference.Target.PreDefinedValues.IMAGE_DETAIL) {
                            when(actionString) {

                                // 뒤로가기 버튼이 눌렸을 경우
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    mViewDataBinding.imageDetailWebView.let {
                                        imageDetailWebView ->
                                        if(imageDetailWebView.canGoBack() && imageDetailWebView.url != mImageDetailViewModel.mKakaoImageModel.docUrl) imageDetailWebView.goBack()
                                        else application.sendBroadcast(Intent().apply {
                                            action = MainBroadcastPreference.Action.CLOSE_IMAGE_DETAIL_FRAGMENT
                                            putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
                                        })
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    // BaseFragment 에서 상속받아 사용하는 기본적인 메소드입니다.
    override fun getLayoutId(): Int = R.layout.fragment_image_detail
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ImageDetailViewModel = mImageDetailViewModel




    // 전체 프래그먼트 설정 순서입니다. onViewCreated 에서 실행됩니다.
    override fun setUp() {
        setBroadcastReceiver()
        setCollapsingToolBar()
        setWebView()
        setViewModelListener()
    }




    /** 방송 수신자를 등록합니다. */
    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mImageDetailBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    /**
     * Fragment 생명주기 onDestroy 에 다음의 내용을 실행합니다.
     * 앞서 등록한 방송수신자를 제거합니다.
     */
    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mImageDetailBroadcastReceiver)
    }




    /**
     * Collapsing Tool Bar 를 설정합니다.
     * Tool Bar 타이틀의 텍스트와 Typeface 를 설정하고 Tool Bar 가 확장 될 경우는
     * 투명하게 보이도록 해줍니다.
     */
    private fun setCollapsingToolBar() {
        mViewDataBinding.imageDetailCollapsingAppToolBar.apply {
            title = getString(R.string.image_detail_sub_title)
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleColor(ContextCompat.getColor(context, R.color.colorTransparent))
        }
    }


    /**
     * 웹 뷰의 기본적인 설정들을 지정합니다.
     * 1. JS 사용 가능 여부 - True
     * 2. POP Up 창 사용 가능 여부 - True (이 부분은 조금 더 고려해봐야 합니다.)
     * 3. 웹의 사진을 표현할지 여부 - True
     * 4. 모바일 지원이 안 되는 웹을 모바일 화면에 끼워 맞추기 - True
     */
    private fun setWebView() {
        mViewDataBinding.imageDetailWebView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            setSupportZoom(true)

        }
        Handler().postDelayed({ mImageDetailViewModel.mIsWebViewLoading.set(false) }, 3000)
    }


    /**
     * View Model 에서 추가적으로 지정해줘야 하는 리스너들을 등록합니다.
     * 현재 상세정보 버튼을 눌렸을 경우 Fragment 에서 처리하도록 하며
     * Dialog 에 ImageModel 의 정보를 담아서 화면에 보이도록 합니다.
     */
    private fun setViewModelListener() {
        mImageDetailViewModel.apply {
            onInfoButtonClickListener = {
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.image_detail_dialog_title)
                    setMessage(
                        "${getString(R.string.image_detail_info_collection, mKakaoImageModel.collection)}\n" +
                        "${getString(R.string.image_detail_info_datetime, mKakaoImageModel.date)}\n" +
                        "${getString(R.string.image_detail_info_display_sitename, mKakaoImageModel.displaySitename)}\n" +
                        "${getString(R.string.image_detail_info_height, mKakaoImageModel.height)}\n" +
                        getString(R.string.image_detail_info_width, mKakaoImageModel.width)
                    )
                    setPositiveButton(R.string.close, null)
                    show()
                }
            }
        }
    }

    companion object {
        /** 새로운 프래그먼트를 생성합니다. */
        fun newInstance(application: Application, imageModel: KakaoImageModel, imageOperationController: ImageOperationController) = ImageDetailFragment(application, imageModel, imageOperationController)
    }

}

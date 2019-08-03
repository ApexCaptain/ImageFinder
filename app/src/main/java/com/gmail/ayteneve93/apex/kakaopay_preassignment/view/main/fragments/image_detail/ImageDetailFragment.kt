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
import androidx.databinding.library.baseAdapters.BR

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageDetailBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import kotlinx.android.synthetic.main.fragment_image_list.view.*

@Suppress("SetJavaScriptEnabled")
class ImageDetailFragment(application : Application, imageModel: KakaoImageModel) : BaseFragment<FragmentImageDetailBinding, ImageDetailViewModel>() {

    private val mImageDetailViewModel : ImageDetailViewModel = ImageDetailViewModel(application, imageModel)

    private val mImageDetailBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                    actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                        target ->
                        if(target == MainBroadcastPreference.Target.PredefinedValues.IMAGE_DETAIL) {
                            when(actionString) {

                                // 뒤로가기 버튼이 눌렸을 경우
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    mViewDataBinding.detailWebView.let {
                                        imageDetailWebView ->

                                        if(imageDetailWebView.canGoBack() && imageDetailWebView.url != mImageDetailViewModel.mKakaoImageModel.docUrl) imageDetailWebView.goBack()
                                        else application.sendBroadcast(Intent().apply {
                                            action = MainBroadcastPreference.Action.CLOSE_IMAGE_DETAIL_FRAGMENT
                                            putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY)
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

    override fun getLayoutId(): Int = R.layout.fragment_image_detail
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ImageDetailViewModel = mImageDetailViewModel

    override fun setUp() {
        setBroadcastReceiver()
        setCollapsingToolBar()
        setWebView()
        setViewModelListener()
    }

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

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mImageDetailBroadcastReceiver)
    }

    private fun setCollapsingToolBar() {
        mViewDataBinding.collapsingToolBar.apply {
            title = getString(R.string.image_detail_sub_title)
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleColor(ContextCompat.getColor(context, R.color.colorTransparent))
        }
    }

    private fun setWebView() {
        mViewDataBinding.detailWebView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            setSupportZoom(true)

        }
        Handler().postDelayed({ mImageDetailViewModel.mIsWebViewLoading.set(false) }, 3000)
    }

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
        fun newInstance(application: Application, imageModel: KakaoImageModel) = ImageDetailFragment(application, imageModel)
    }

}

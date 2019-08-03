package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_detail


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.databinding.library.baseAdapters.BR

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageDetailBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import org.koin.android.ext.android.inject

class ImageDetailFragment : BaseFragment<FragmentImageDetailBinding, ImageDetailViewModel>() {

    private val mImageDetailViewModel : ImageDetailViewModel by inject()

    private val mImageDetailBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_image_detail
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ImageDetailViewModel = mImageDetailViewModel

    fun receiveImageModel(imageModel : KakaoImageModel) : ImageDetailFragment {
        return this
    }

    override fun setUp() {

    }

    companion object {
        fun newInstance() = ImageDetailFragment()
    }

}

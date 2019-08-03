package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageListItemViewModel(
    application: Application
) : BaseViewModel(application){

    lateinit var mKakaoImageModel : KakaoImageModel
    lateinit var mImageSizePercentage : ObservableField<Float>
    lateinit var onImageItemClickListener : () -> Unit

    fun onImageItemClick() {
        onImageItemClickListener()
    }
    fun onImageItemLongClick(view : View) : Boolean {
        return false
    }

}
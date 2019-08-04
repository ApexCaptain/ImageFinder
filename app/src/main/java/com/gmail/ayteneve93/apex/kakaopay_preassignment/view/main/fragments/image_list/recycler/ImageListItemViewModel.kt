package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageListItemViewModel(
    application: Application,
    private val mImageOperationController: ImageOperationController
) : BaseViewModel(application){

    private val mApplication = application
    lateinit var mKakaoImageModel : KakaoImageModel
    lateinit var mImageSizePercentage : ObservableField<Float>
    lateinit var mIsOnMultipleSelectionMode : ObservableField<Boolean>
    fun setEventHandlerOnSelectionModeChanged() {
        mIsOnMultipleSelectionMode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(!mIsOnMultipleSelectionMode.get()!!) mIsItemSelected.set(false)
            }
        })
    }

    lateinit var mIsItemSelected : ObservableField<Boolean>
    private var mClickModeChanged = false
    lateinit var onImageItemClickListener : () -> Unit
    fun boundOnImageItemClick() {
        if(mIsOnMultipleSelectionMode.get()!!) {
            if(mClickModeChanged) mClickModeChanged = false
            else mIsItemSelected.set(!mIsItemSelected.get()!!)

        } else onImageItemClickListener()
    }
    fun boundOnImageItemLongClick(view : View) : Boolean {
        if(!mIsOnMultipleSelectionMode.get()!!) {
            mIsOnMultipleSelectionMode.set(true)
            (mApplication.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VibrationEffect.createOneShot(100, 100))
            mIsItemSelected.set(true)
            mClickModeChanged = true
            mApplication.sendBroadcast(Intent().apply {
                action = MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED
                putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY)
                putExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY, MainBroadcastPreference.Extra.ImageItemSelectionMode.PredefinedValues.SELECTION_MODE)
            })
            mApplication.sendBroadcast(Intent().apply {
                action = MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED
                putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                putExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY, MainBroadcastPreference.Extra.ImageItemSelectionMode.PredefinedValues.SELECTION_MODE)
            })
        }
        return false
    }
    fun boundOnImageItemCheckedChanged(isChecked : Boolean) {
        mIsItemSelected.set(isChecked)
        if(isChecked) mImageOperationController.addImageModel(mKakaoImageModel)
        else mImageOperationController.removeImageModel(mKakaoImageModel)
    }

}
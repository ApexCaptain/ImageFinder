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

/**
 * ImageListRecycler 에서 사용하는 Item ViewModel 입니다.
 * @property mImageOperationController 이미지 공유/정보 제어 객체입니다
 * @property mApplication 애플리케이션 객체입니다. 진동과 Broadcast 를 위해 사용합니다.
 * @property mKakaoImageModel 이미지 정보를 담고있는 데이터 모델입니다.
 * @property mImageSizePercentage 이미지 확대 수치입니다.
 * @property mIsOnMultipleSelectionMode 이미지 선택 모드가 다중 선택인지 여부를 지정하는 Observable<Boolean> 객체입니다.
 * @property mIsItemSelected 현재의 아이템이 선택되었는지 여부를 지정하는 Observable<Boolean> 객체입니다.
 * @property mClickModeChanged 클릭 모드의 변경을 지정하는 Boolean 변수입니다. LongClick 이벤트로 선택 모드를 변경하는데,
 *                              해당 이벤트가 끝난 뒤 일반 Click 이벤트가 함께 발생해서 이를 막기 위해 생성되었습니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageListItemViewModel(
    application: Application,
    private val mImageOperationController: ImageOperationController
) : BaseViewModel(application){

    private val mApplication = application
    lateinit var mKakaoImageModel : KakaoImageModel
    lateinit var mImageSizePercentage : ObservableField<Float>
    lateinit var mIsOnMultipleSelectionMode : ObservableField<Boolean>
    lateinit var mIsItemSelected : ObservableField<Boolean>
    private var mClickModeChanged = false

    /** 선택 모드가 변경될 때 마다 해당 내용을 뷰에 반영해줍니다. */
    fun setEventHandlerOnSelectionModeChanged() {
        mIsOnMultipleSelectionMode.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(!mIsOnMultipleSelectionMode.get()!!) mIsItemSelected.set(false)
            }
        })
    }

    // Layout 과 바인딩 된 메소드
    lateinit var onImageItemClickListener : () -> Unit
    /** 이미지 클릭시 처리는 Adapter에서 지정한 리스너에서 처리합니다. */
    fun boundOnImageItemClick() {
        if(mIsOnMultipleSelectionMode.get()!!) {
            if(mClickModeChanged) mClickModeChanged = false
            else mIsItemSelected.set(!mIsItemSelected.get()!!)

        } else onImageItemClickListener()
    }

    /** 이미지를 길게 누르면 Broadcast 로 해당 내용을 전파하고 선택 모드를 다중으로 변경합니다. */
    fun boundOnImageItemLongClick(@Suppress(ConstantUtils.SuppressWarningAttributes.UNUSED_PARAMETER)view : View) : Boolean {
        if(!mIsOnMultipleSelectionMode.get()!! && !mImageOperationController.mIsOnOperation.get()!!) {
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

    /** 다중 선택 모드에서 체크가 해제 혹은 등록 되었을 때 이미지 공유/다운로드 제어 객체에 이미지 모델을 등록해줍니다. */
    fun boundOnImageItemCheckedChanged(isChecked : Boolean) {
        mIsItemSelected.set(isChecked)
        if(isChecked) mImageOperationController.addImageModel(mKakaoImageModel)
        else mImageOperationController.removeImageModel(mKakaoImageModel)
    }

}
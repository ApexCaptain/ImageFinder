package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ItemImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * ImageListFragmnet 의 Recycler View 에 등록되는 RecyclerAdpater 입니다.
 *
 * @property application 애플리케이션 객체입니다. 시스템 서비스 호출, 리소스 조회 및 Broadcast 에 사용됩니다.
 * @property mKakaoImageModelManager 카카오 Api 에서 이미지 모델들을 추출하기 위한 관리자 객체입니다.
 * @property mPreferenceUtils 사용자 설정 정보 Utility 객체입니다.
 * @property mImageOperationController 이미지 공유/다운로드 제어기 객체입니다.
 * @property mImageListItemViewModelList 카카오 이미지 검색 Api 에서 가져오는 전체 정보를 담는 데이터 리스트입니다.
 * @property mCompositeDisposable Rx 작업 종료 후 Disposable 객체를 모아두었다 한 번에 처리하기 위한 CompositeDisposable 입니다.
 * @property mAnimAppearMills 각각의 이미지 아이템이 생성될 때 애니메이션의 지속시간입니다.
 * @property mImageSizePercentage 사용자 정보에 저장된 이미지의 Percent 크기입니다.
 * @property mImageResizeOffset 한 번 Resize 를 수행할 때 Percent 간격입니다.
 * @property mMaxImageSizePercentage 각각의 이미지가 가질 수 있는 최대한도의 Percent 크기입니다.
 * @property mMinImageSizePercentage 각각의 이미지가 가질 수 있는 최소한도의 Percent 크기입니다.
 * @property mMinImageSizePixels 각각의 이미지가 가질 수 있는 최소한도의 Pixel 크기입니다.
 * @property mMaxPortraitColumnCount Portrait 상태의 화면에서 최대한도의 이미지 열의 갯수입니다.
 * @property mMinPortraitColumnCount Portrait 상태의 화면에서 최소한도의 이미지 열의 갯수입니다.
 * @property mDisableAppearAnim 이미지 아이템의 애니메이션을 비활성화 할지 여부의 Boolean 입니다.
 * @property mDiasbleAppearAnimHandler 이미지 아이템의 애니메이션 비활성화를 시간차를 두고 풀어주기 위한 Handler 객체입니다.
 * @property mIsOnMultipleSelectionMode 현재 다중선택 모드인지를 알려주는 Observable<Boolean> 객체입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageListRecyclerAdapter(
    private val application : Application,
    private val mKakaoImageModelManager : KakaoImageModelManager,
    private val mPreferenceUtils: PreferenceUtils,
    private val mImageOperationController: ImageOperationController
) : RecyclerView.Adapter<ImageListRecyclerAdapter.ImageListItemViewHolder>(){

    private val mImageListItemViewModelList : ArrayList<ImageListItemViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()
    private val mAnimAppearMills = 800L
    private val mImageSizePercentage = ObservableField(mPreferenceUtils.getImageSizePercentage())
    private val mImageResizeOffset = 0.02f
    private val mMaxImageSizePercentage = 1.0f
    private val mMinImageSizePercentage = 0.7f
    private val mMinImageSizePixels = 450
    private val mMaxPortraitColumnCount : Int by lazy {
        Resources.getSystem().displayMetrics.let {
            val widthPixels = it.widthPixels
            val heightPixels = it.heightPixels
            if(widthPixels > heightPixels) heightPixels/mMinImageSizePixels
            else widthPixels/mMinImageSizePixels
        }
    }
    private val mMinPortraitColumnCount = 1
    private var mDisableAppearAnim = false
    private val mDiasbleAppearAnimHandler = Handler()
    private val mIsOnMultipleSelectionMode = ObservableField(false)

    /** 현재 총 몇 개의 아이템이 있는지 갯수를 확인합니다. */
    override fun getItemCount(): Int = mImageListItemViewModelList.size

    /** 각각의 뷰 홀더를 Bind 해주고 Tag 설정 후 필요시 애니메이션을 출력합니다. */
    override fun onBindViewHolder(holder: ImageListItemViewHolder, position: Int) {
        val eachImageListItemViewModel = mImageListItemViewModelList[position]
        holder.apply {
            bind(eachImageListItemViewModel)
            itemView.tag = eachImageListItemViewModel
            if(!mDisableAppearAnim) itemView.startAnimation(AnimationUtils.loadAnimation(application, R.anim.anim_item_image).apply {
                duration = mAnimAppearMills
            })
        }
    }

    /** 각가의 뷰 홀더를 Inflate 로 Layout과 함께 생성 해줍니다. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListItemViewHolder {
        return ImageListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_image_list, parent,false
            )
        )
    }

    /**
     * 이미지를 검색합니다.
     *
     * @param queryKeyword 검색할 문자열입니다.
     * @param sortOption 검색 정렬 옵션입니다.
     * @param pageNumber 검색 페이지 번호입니다.
     * @param size 한 페이지에 표시될 이미지 갯수입니다.
     * @param onSearchResult 검색 결과를 호출자에게 전달해주는 콜백입니다.
     *
     * */
    fun searchImage(queryKeyword : String, sortOption: KakaoImageSortOption, pageNumber : Int, size : Int,
                    onSearchResult : (isError : Boolean, errorMessage : String?, isEmpty : Boolean, isEnd : Boolean) -> Unit) {
        mImageListItemViewModelList.clear()
        mCompositeDisposable.clear()
        mCompositeDisposable.add(
            mKakaoImageModelManager.rxKakaoImageSearchByKeyword(queryKeyword, sortOption, pageNumber, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        it.documents.forEach { eachKakaoImageModel ->
                            mImageListItemViewModelList.add(ImageListItemViewModel(application, mImageOperationController).apply {
                                mKakaoImageModel = eachKakaoImageModel
                                mImageSizePercentage = this@ImageListRecyclerAdapter.mImageSizePercentage
                                mIsOnMultipleSelectionMode = this@ImageListRecyclerAdapter.mIsOnMultipleSelectionMode
                                mIsItemSelected = ObservableField(mImageOperationController.isImageModelExists(mKakaoImageModel))
                                setEventHandlerOnSelectionModeChanged()
                                onImageItemClickListener = {
                                    application.sendBroadcast(Intent().apply {
                                        action = MainBroadcastPreference.Action.IMAGE_ITEM_CLICKED
                                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY)
                                        putExtra(MainBroadcastPreference.Extra.ImageItem.KEY, mKakaoImageModel)
                                    })
                                }
                            })
                        }
                        notifyDataSetChanged()
                        onSearchResult(false, null, mImageListItemViewModelList.isEmpty(), it.isEnd)
                    },
                    {
                        notifyDataSetChanged()
                        onSearchResult(true, it.message, true, true)
                    })
        )
    }

    /** 리스트의 내용을 모두 정리하고 Disposable 객체를 Clear 해줍니다. */
    fun clear() {
        mImageListItemViewModelList.clear()
        mCompositeDisposable.clear()
        notifyDataSetChanged()
    }

    /**
     * Pinch 이벤트에 따라 이미지 크기와 열의 갯수를 조정해줍니다. 열의 갯수가 변할 때는
     * 가벼운 진동도 발생시킵니다.
     *
     * @param isZoomIn 이벤트가 ZoomIn 인지 ZoomOut 인지 판단합니다.
     * @param notifyColumnCountChanged 열의 갯수가 변경될 경우 이를 콜백으로 호출자에게 전달해줍니다.
     */
    fun resizeOnPinch(isZoomIn : Boolean, notifyColumnCountChanged : () -> Unit) {
        val prevSize = mImageSizePercentage.get()!!
        val prevPortraitImageColumnCount = mPreferenceUtils.getImageColumnCount()
        if(isZoomIn) {
            (prevSize + mImageResizeOffset).let { magnifiedImageSize ->
                if(magnifiedImageSize > mMaxImageSizePercentage) {
                    if(prevPortraitImageColumnCount <= mMinPortraitColumnCount) return
                    mImageSizePercentage.set(mMinImageSizePercentage)
                    mPreferenceUtils.setImageColumnCount(prevPortraitImageColumnCount - 1)
                    mDisableAppearAnim = true
                    mDiasbleAppearAnimHandler.removeCallbacksAndMessages(null)
                    mDiasbleAppearAnimHandler.postDelayed({ mDisableAppearAnim = false }, mAnimAppearMills)
                    notifyColumnCountChanged()
                    (application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                        .vibrate(VibrationEffect.createOneShot(100, 100))
                } else mImageSizePercentage.set(magnifiedImageSize)
            }
        } else {
            (prevSize - mImageResizeOffset).let { reducedImageSize ->
                if(reducedImageSize < mMinImageSizePercentage) {
                    if(prevPortraitImageColumnCount >= mMaxPortraitColumnCount) return
                    mImageSizePercentage.set(mMaxImageSizePercentage)
                    mPreferenceUtils.setImageColumnCount(prevPortraitImageColumnCount + 1)
                    mDisableAppearAnim = true
                    mDiasbleAppearAnimHandler.removeCallbacksAndMessages(null)
                    mDiasbleAppearAnimHandler.postDelayed({ mDisableAppearAnim = false }, mAnimAppearMills)
                    notifyColumnCountChanged()
                    (application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                        .vibrate(VibrationEffect.createOneShot(100, 100))
                } else mImageSizePercentage.set(reducedImageSize)
            }
        }

        mPreferenceUtils.setImageSizePercentage(mImageSizePercentage.get()!!)
    }

    /** 선택 모드를 일반 혹은 다중 선택으로 변경해줍니다. */
    fun setSelectionMode(selectionMode : Boolean) {
        mIsOnMultipleSelectionMode.set(selectionMode)
        if(!selectionMode) mImageOperationController.clearImageModels()
    }

    /**
     * 이미지 리스트 아이템의 뷰 홀더입니다.
     * 일반 뷰 홀더와 달리 각각의 뷰의 viewModel 을 바인딩 해줍니다.
     */
    class ImageListItemViewHolder(
        private val binding : ItemImageListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : ImageListItemViewModel) {
            binding.apply {
                viewModel = item
            }
        }
    }

}
package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.content.res.Resources
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ItemImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.ConstantUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ImageListRecyclerAdapter(
    private val application : Application,
    private val mKakaoImageModelManager : KakaoImageModelManager,
    private val mPreferenceUtils: PreferenceUtils
) : RecyclerView.Adapter<ImageListRecyclerAdapter.ImageListItemViewHolder>(){

    private val mImageListItemViewModelList : ArrayList<ImageListItemViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()
    private val mAnimAppearMills = 800L
    private val mImageSizePercentage = ObservableField(mPreferenceUtils.getImageSizePercentage())
    private val mImageResizeOffset = 0.02f
    private val mMaxImageSizePercentage = 1.0f
    private val mMinImageSizePercentage = 0.5f
    private val mMinImageSizePixels = 400
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

    override fun getItemCount(): Int = mImageListItemViewModelList.size

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListItemViewHolder {
        return ImageListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_image_list, parent,false
            )
        )
    }

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
                            mImageListItemViewModelList.add(ImageListItemViewModel(application).apply {
                                mKakaoImageModel = eachKakaoImageModel
                                mImageSizePercentage = this@ImageListRecyclerAdapter.mImageSizePercentage
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

    fun clear() {
        mImageListItemViewModelList.clear()
        mCompositeDisposable.clear()
        notifyDataSetChanged()
    }

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
                } else mImageSizePercentage.set(reducedImageSize)
            }
        }

        mPreferenceUtils.setImageSizePercentage(mImageSizePercentage.get()!!)
    }

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
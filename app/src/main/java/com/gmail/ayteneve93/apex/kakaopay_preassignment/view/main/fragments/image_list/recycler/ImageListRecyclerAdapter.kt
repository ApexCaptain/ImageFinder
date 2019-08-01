package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ItemImageListBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@Suppress("spellCheckingInspection")
class ImageListRecyclerAdapter(
    private val application : Application,
    private val mKakaoImageModelManager : KakaoImageModelManager
) : RecyclerView.Adapter<ImageListRecyclerAdapter.ImageListItemViewHolder>(){

    private val mImageListItemViewModelList : ArrayList<ImageListItemViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()
    private val mAnimAppearMills = 800L

    override fun getItemCount(): Int = mImageListItemViewModelList.size

    override fun onBindViewHolder(holder: ImageListItemViewHolder, position: Int) {
        val eachImageListItemViewModel = mImageListItemViewModelList[position]
        holder.apply {
            bind(eachImageListItemViewModel)
            itemView.tag = eachImageListItemViewModel
            itemView.startAnimation(AnimationUtils.loadAnimation(application, R.anim.anim_item_image).apply {
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

    fun searchImage(queryKeyword : String, sortOption: KakaoImageSortOption, page : Int, size : Int, onSearchResult : (isError : Boolean, errorMessage : String?, isEmpty : Boolean, isEnd : Boolean) -> Unit) {
        mImageListItemViewModelList.clear()
        mCompositeDisposable.add(
            mKakaoImageModelManager.rxKakaoImageSearchByKeyword(queryKeyword, sortOption, page, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        it.documents.forEach { eachKakaoImageModel ->
                            mImageListItemViewModelList.add(ImageListItemViewModel(application).apply {
                                mKakaoImageModel = eachKakaoImageModel
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
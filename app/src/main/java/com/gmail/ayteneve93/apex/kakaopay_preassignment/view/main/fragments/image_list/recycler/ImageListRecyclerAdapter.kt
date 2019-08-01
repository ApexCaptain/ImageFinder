package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler

import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ItemImageListBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ImageListRecyclerAdapter(
    private val application : Application,
    private val mKakaoImageModelManager : KakaoImageModelManager
) : RecyclerView.Adapter<ImageListRecyclerAdapter.ImageListItemViewHolder>(){

    private val mImageListItemViewModelList : ArrayList<ImageListItemViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()

    override fun getItemCount(): Int = mImageListItemViewModelList.size

    override fun onBindViewHolder(holder: ImageListItemViewHolder, position: Int) {
        val eachImageListItemViewModel = mImageListItemViewModelList[position]
        holder.apply {
            bind(eachImageListItemViewModel)
            itemView.tag = eachImageListItemViewModel
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListItemViewHolder {
        return ImageListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_image_list, parent,false
            )
        )
    }

    fun test() {
        for(x in 1..100) {
            mImageListItemViewModelList.add(ImageListItemViewModel(application))
        }
        Log.d("ayteneve93_test", "notify")
        notifyDataSetChanged()
    }

    @Suppress("spellCheckingInspection")
    fun searchImage(keyword : String, sortOption: KakaoImageSortOption, page : Int, size : Int = 20) {
        mImageListItemViewModelList.clear()
        mCompositeDisposable.add(
            mKakaoImageModelManager.rxKakaoImageSearchByKeyword(keyword, sortOption, page, size)
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
                    },
                    {

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
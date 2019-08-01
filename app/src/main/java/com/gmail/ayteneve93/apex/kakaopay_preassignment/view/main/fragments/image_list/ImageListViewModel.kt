package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

class ImageListViewModel(
    application: Application
) : BaseViewModel(application){

    private val mApplication : Application = application

    var mTitle = MutableLiveData<CharSequence>().apply { value = application.getString(R.string.search_list_title_hint) }
    var mAbnormalResultMessage = MutableLiveData<CharSequence>().apply { value = application.getString(R.string.no_search_result_caption) }
    private lateinit var mRecentQueryKeyword : String
    var mSortOption = ObservableField(KakaoImageSortOption.ACCURACY)
    var mPage = ObservableField(1)
    var mNoSearchResult = ObservableField(false)

    lateinit var onQueryChangedListener : (queryKeyword : String, sortOption : KakaoImageSortOption, page : Int) -> Unit
    fun inputNewKeyword(queryKeyword : String) {
        mRecentQueryKeyword = queryKeyword
        mTitle.value = mApplication.getString(R.string.searching)
        onQueryChangedListener(queryKeyword, mSortOption.get()!!, mPage.get()!!)
    }
    fun setSearchResult(isError : Boolean, errorMessage : String?, isEmpty : Boolean, isEnd : Boolean) {
        if(isError) {
            mNoSearchResult.set(isError)
            errorMessage?.let { mAbnormalResultMessage.value = it }
            mTitle.value = mApplication.getString(R.string.search_error_title)
            return
        }
        mNoSearchResult.set(isEmpty)
        mAbnormalResultMessage.value = mApplication.getString(R.string.no_search_result_caption)
        if(isEmpty) mTitle.value = mApplication.getString(R.string.no_search_result_title, mRecentQueryKeyword)
        else mTitle.value = mApplication.getString(R.string.search_list_title, mRecentQueryKeyword)
    }
}
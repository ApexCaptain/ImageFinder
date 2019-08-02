package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel
import org.koin.ext.isInt


class ImageListViewModel(
    application: Application,
    mPreferenceUtils: PreferenceUtils
) : BaseViewModel(application){

    private val mApplication : Application = application
    private val initialPageNumber = 1
    private val maxPageNumber = 50

    var mSearchResultTitle = MutableLiveData<CharSequence>().apply {
        value = if(::mRecentQueryKeyword.isInitialized) mApplication.getString(R.string.search_list_title, mRecentQueryKeyword)
                else application.getString(R.string.search_list_title_hint)
    }
    var mAbnormalResultMessage = MutableLiveData<CharSequence>().apply { value = application.getString(R.string.no_search_result_caption) }
    var mPageNumberText = MutableLiveData<CharSequence>()
    var mNoSearchResult = ObservableField(false)
    var mSortOption = mPreferenceUtils.getSortOption()
    var mPrevPageButtonAvailability = ObservableField(false)
    var mNextPageButtonAvailability = ObservableField(false)
    var mPageButtonVisibility = ObservableField(false)
    var mPageNumber = initialPageNumber
    var mDisplayCount = mPreferenceUtils.getDisplayCount()
    private lateinit var mRecentQueryKeyword : String

    lateinit var onQueryChangedListener : (queryKeyword : String, sortOption : KakaoImageSortOption, pageNumber : Int, displayCount : Int) -> Unit
    fun inputNewKeyword(queryKeyword : String) {
        mRecentQueryKeyword = queryKeyword
        mSearchResultTitle.value = mApplication.getString(R.string.searching)
        onQueryChangedListener(queryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    fun setSearchResult(isError : Boolean, errorMessage : String?, pageNumber : Int, isEmpty : Boolean, isEnd : Boolean) {
        if(isError) {
            mPageButtonVisibility.set(false)
            mNoSearchResult.set(isError)
            errorMessage?.let { mAbnormalResultMessage.value = it }
            mSearchResultTitle.value = mApplication.getString(R.string.search_error_title)
            return
        }
        mPageNumber = pageNumber
        mPageNumberText.value = mApplication.getString(R.string.page_count, mPageNumber)
        mNoSearchResult.set(isEmpty)
        mAbnormalResultMessage.value = mApplication.getString(R.string.no_search_result_caption)
        if(isEmpty) mSearchResultTitle.value = mApplication.getString(R.string.no_search_result_title, mRecentQueryKeyword)
        else mSearchResultTitle.value = mApplication.getString(R.string.search_list_title, mRecentQueryKeyword)

        if(isEmpty) mPageButtonVisibility.set(false)
        else {
            mPageButtonVisibility.set(true)
            if(mPageNumber == initialPageNumber) mPrevPageButtonAvailability.set(false)
            else mPrevPageButtonAvailability.set(true)
            if(mPageNumber == maxPageNumber || isEnd) mNextPageButtonAvailability.set(false)
            else mNextPageButtonAvailability.set(true)
        }
    }

    fun changeSortOption(sortOption: KakaoImageSortOption) {
        mSortOption = sortOption
        if(::mRecentQueryKeyword.isInitialized) onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    fun changeDisplayCount(displayCount : Int) {
        val prevDisplayCount = mDisplayCount
        mDisplayCount = displayCount
        if(::mRecentQueryKeyword.isInitialized)  {
            mPageNumber = kotlin.math.ceil(prevDisplayCount * mPageNumber / displayCount.toDouble()).toInt()
            if(mPageNumber > maxPageNumber) mPageNumber = maxPageNumber
            onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
        }
    }

    // Layout 과 바인딩 된 메소드
    fun boundOnPrevPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber - 1, mDisplayCount)
    }
    fun boundOnNextPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber + 1, mDisplayCount)
    }
}
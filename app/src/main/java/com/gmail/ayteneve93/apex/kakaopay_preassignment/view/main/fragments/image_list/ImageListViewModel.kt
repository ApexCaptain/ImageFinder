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

/**
 * ImageListFragment 의 ViewModel 입니다.
 *
 * @property mApplication 애플리케이션 객체입니다. Resource 참조에 사용합니다.
 * @property initialPageNumber 최초 페이지 숫자입니다. 1로 고정된 값입니다.
 * @property maxPageNumber 최대 페이지 숫자입니다. 50으로 고정된 값입니다.
 * @property mSearchResultTitle 검색 결과의 제목입니다. 기본값은 검색을 유도하는 String 이 지정되어 있습니다.
 * @property mAbnormalResultMessage 검색 결과에 문제가 생겼을 경우의 텍스트입니다. 기본 값은 검색 결과가 없음을 알리는 String 이 지정되어 있습니다.
 * @property mPageNumberText 페이지 넘버를 보여주는 텍스틍비니다.
 * @property mNoSearchResult 검색 결과가 없을 경우를 확인하는 Observable<Boolean> 객체입니다.
 * @property mSortOption 검색 정렬 기준입니다.
 * @property mPrevPageButtonAvailability 전 페이지로 이동하는 버튼의 활성화 여부인 Observable<Boolean> 객체입니다.
 * @property mNextPageButtonAvailability 다음 페이지로 이동하는 버튼의 활성화 여부인 Observable<Boolean> 객체입니다.
 * @property mPageButtonVisibility 페이지 버튼 전체의 가시성 여부를 지정하는 Observable<Boolean> 객체입니다.
 * @property mPageNumber 현재 페이지 번호입니다.
 * @property mDisplayCount 화면에 표시되는 이미지의 갯수 입니다.
 * @property mFilterMenuVisibility Filter Menu 의 가시성 여부를 지정하는 Observable<Boolean> 객체입니다.
 * @property mRecentQueryKeyword 최근에 입력된 검색어입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
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
    var mFilterMenuVisibility = ObservableField(false)
    private lateinit var mRecentQueryKeyword : String

    lateinit var onQueryChangedListener : (queryKeyword : String, sortOption : KakaoImageSortOption, pageNumber : Int, displayCount : Int) -> Unit
    /**
     * 새로운 검색어가 입력된 경우 검색어를 저장하고
     * 타이틀을 검색중으로 표시합니다. 실제 검색 절차는 Fragment 에서 등록한
     * 리스너로 처리합니다.
     */
    fun inputNewKeyword(queryKeyword : String) {
        mRecentQueryKeyword = queryKeyword
        mSearchResultTitle.value = mApplication.getString(R.string.searching)
        onQueryChangedListener(queryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    /**
     * 검색된 결과를 받아와 해당 내용을 바탕으로 전체 뷰의
     * 값들을 그에 맞춰 변경해줍니다.
     */
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

    /**
     * 검색 정렬 옵션이 변결될 경우 이를 저장하고
     * 리스너를 통해 검색 절차를 재 수행합니다.
     */
    fun changeSortOption(sortOption: KakaoImageSortOption) {
        mSortOption = sortOption
        if(::mRecentQueryKeyword.isInitialized) onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    /**
     * 한 페이지당 표시 이미지 갯수가 변경 된 경우 이를 저장하고
     * 리스너를 통해 검색 절차를 재 수행합니다. 단, 이 때
     * 기존 페이지 수와 표시 갯수를 기반으로 페이지 번호 역시 변경해줍니다.
     * 가령 30개씩 보고있는 상태에서 2페이지를 보고 있을 때 표시 갯수를 80개로 변경할 경우
     * 1페이지로 바꿔줍니다.
     */
    fun changeDisplayCount(displayCount : Int) {
        val prevDisplayCount = mDisplayCount
        mDisplayCount = displayCount
        if(::mRecentQueryKeyword.isInitialized)  {
            mPageNumber = kotlin.math.ceil(prevDisplayCount * mPageNumber / displayCount.toDouble()).toInt()
            if(mPageNumber > maxPageNumber) mPageNumber = maxPageNumber
            onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
        }
    }

    /**
     * 단순 Refresh 요청. 현재 저장된 값들을 기반으로
     * 검색을 다시 수행합니다.
     */
    fun refresh() {
        if(::mRecentQueryKeyword.isInitialized) {
            onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
        }
    }

    /** 필터 메뉴가 보이게 설정합니다.*/
    fun showFilterMenu() {
        mFilterMenuVisibility.set(true)
    }

    /** 필터 메뉴가 보이지 않게 설정합니다.*/
    fun hideFilterMenu() {
        mFilterMenuVisibility.set(false)
    }

    // Layout 과 바인딩 된 메소드
    /** 이전 페이지로 이동합니다.*/
    fun boundOnPrevPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber - 1, mDisplayCount)
    }

    /** 다음 페이지로 이동합니다.*/
    fun boundOnNextPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber + 1, mDisplayCount)
    }
}
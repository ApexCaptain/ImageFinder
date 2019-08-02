package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ImageListFragment : BaseFragment<FragmentImageListBinding, ImageListViewModel>() {

    private val mImageListViewModel : ImageListViewModel by viewModel()
    private val mImageListRecyclerAdapter :ImageListRecyclerAdapter by inject()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private val mColumnCountRatio : Int by lazy {
        Resources.getSystem().displayMetrics.let {
            val widthPixels = it.widthPixels.toDouble()
            val heightPixels = it.heightPixels.toDouble()
            if(widthPixels > heightPixels) (widthPixels/heightPixels).roundToInt()
            else (heightPixels/widthPixels).roundToInt()
        }
    }

    private lateinit var mImageListRecyclerDecoration : RecyclerView.ItemDecoration

    private val mImageListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                    actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                        target ->
                        if(target == MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST) {
                            when(actionString) {

                                // 새로운 검색어 입력됨
                                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT -> {
                                    val queryKeyword = intent.getStringExtra(MainBroadcastPreference.Extra.QueryString.KEY)
                                    queryKeyword?.let {
                                        mImageListViewModel.inputNewKeyword(queryKeyword)
                                    }
                                }

                                // 정렬 기준이 변경됨
                                MainBroadcastPreference.Action.SORT_OPTION_CHANGED -> {
                                    val sortOption = intent.getSerializableExtra(MainBroadcastPreference.Extra.SortOption.KEY) as KakaoImageSortOption
                                    mImageListViewModel.changeSortOption(sortOption)
                                }

                                MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED -> {
                                    val displayCount = intent.getIntExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 30)
                                    mImageListViewModel.changeDisplayCount(displayCount)
                                }

                                // 사용자가 화면을 Pinch 함(줌 인 혹은 줌 아웃)
                                MainBroadcastPreference.Action.PINCH -> {
                                    intent.getBooleanExtra(MainBroadcastPreference.Extra.PinchState.KEY, MainBroadcastPreference.Extra.PinchState.PredefinedValues.ZOOM_IN).also {
                                        mImageListRecyclerAdapter.resizeOnPinch(it) {
                                            setRecyclerViewLayoutManager()
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_image_list
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): ImageListViewModel {
        return mImageListViewModel
    }

    override fun setUp() {
        setBroadcastReceiver()
        setImageListRecyclerAdapter()
        setViewModelListener()
    }

    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mImageListBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT,
                MainBroadcastPreference.Action.SORT_OPTION_CHANGED,
                MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED,
                MainBroadcastPreference.Action.PINCH
            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mImageListBroadcastReceiver)
        mImageListRecyclerAdapter.clear()
    }

    private fun setImageListRecyclerAdapter() {
       mViewDataBinding.imageListRecyclerView.apply {
           adapter = mImageListRecyclerAdapter
       }
        setRecyclerViewLayoutManager()
    }

    private fun setRecyclerViewLayoutManager() {
        val portraitImageColumnCount = mPreferenceUtils.getImageColumnCount()
        mViewDataBinding.imageListRecyclerView.apply {
            val prevPosition = if(layoutManager != null) (layoutManager as GridLayoutManager).findFirstVisibleItemPosition() else 0
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, portraitImageColumnCount)
            else GridLayoutManager(mActivity, portraitImageColumnCount * mColumnCountRatio)
            (layoutManager as GridLayoutManager).scrollToPosition(prevPosition)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager()
    }

    private fun setViewModelListener() {
        mImageListViewModel.apply {
            onQueryChangedListener = {
                queryKeyword, sortOption, pageNumber, displayCount ->
                mImageListRecyclerAdapter.searchImage(queryKeyword, sortOption, pageNumber, displayCount) {
                    isError, errorMessage, isEmpty, isEnd ->
                    mImageListViewModel.setSearchResult(isError, errorMessage, pageNumber, isEmpty, isEnd)
                }
            }
        }
    }

}






















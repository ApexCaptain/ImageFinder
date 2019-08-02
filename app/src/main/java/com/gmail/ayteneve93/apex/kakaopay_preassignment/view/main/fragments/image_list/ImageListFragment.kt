package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ImageListFragment : BaseFragment<FragmentImageListBinding, ImageListViewModel>() {

    private val mImageListViewModel : ImageListViewModel by viewModel()
    private val mImageListRecyclerAdapter :ImageListRecyclerAdapter by inject()
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

                                // 사용자가 화면을 Pinch 함(줌 인 혹은 줌 아웃)
                                MainBroadcastPreference.Action.PINCH -> {
                                    when(intent.getBooleanExtra(MainBroadcastPreference.Extra.PinchState.KEY, MainBroadcastPreference.Extra.PinchState.PredefinedValues.ZOOM_IN)) {
                                        MainBroadcastPreference.Extra.PinchState.PredefinedValues.ZOOM_IN -> {
                                            Log.d("ayteneve93_test", "줌 인")
                                        }
                                        MainBroadcastPreference.Extra.PinchState.PredefinedValues.ZOOM_OUT -> {
                                            Log.d("ayteneve93_test", "줌 아웃")
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
        setImageListRecyclerDecoration()
        setViewModelListener()
    }

    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mImageListBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT,
                MainBroadcastPreference.Action.SORT_OPTION_CHANGED,
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
    }

    private fun setImageListRecyclerAdapter() {
       mViewDataBinding.imageListRecyclerView.apply {
           adapter = mImageListRecyclerAdapter
           layoutManager =
               if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, 3)
               else GridLayoutManager(mActivity, 5)
           addItemDecoration(object : RecyclerView.ItemDecoration() {
               override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                   val space = 15
                   outRect.apply {
                       left = space
                       right = space
                       bottom = space
                       top = if(parent.getChildLayoutPosition(view) == 0) space else 0
                   }
               }
           })
       }
    }

    private fun setImageListRecyclerDecoration() {
        mImageListRecyclerDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

                super.getItemOffsets(outRect, view, parent, state)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mViewDataBinding.imageListRecyclerView.layoutManager =
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, 3)
            else GridLayoutManager(mActivity, 5)
    }

    private fun setViewModelListener() {
        mImageListViewModel.apply {
            onQueryChangedListener = {
                queryKeyword, sortOption, pageNumber ->
                mImageListRecyclerAdapter.searchImage(queryKeyword, sortOption, pageNumber, 45) {
                    isError, errorMessage, isEmpty, isEnd ->
                    mImageListViewModel.setSearchResult(isError, errorMessage, pageNumber, isEmpty, isEnd)
                }
            }
        }
    }



}






















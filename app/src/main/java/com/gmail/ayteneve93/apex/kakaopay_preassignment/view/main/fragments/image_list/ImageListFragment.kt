package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.GridLayoutManager

import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.FragmentImageListBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainBroadcastPreference
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import com.linroid.filtermenu.library.FilterMenu
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ImageListFragment : BaseFragment<FragmentImageListBinding, ImageListViewModel>() {

    private val mImageListViewModel : ImageListViewModel by viewModel()
    private val mImageListRecyclerAdapter :ImageListRecyclerAdapter by inject()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private val mImageOperationController : ImageOperationController by inject()
    private val mColumnCountRatio : Int by lazy {
        Resources.getSystem().displayMetrics.let {
            val widthPixels = it.widthPixels.toDouble()
            val heightPixels = it.heightPixels.toDouble()
            if(widthPixels > heightPixels) (widthPixels/heightPixels).roundToInt()
            else (heightPixels/widthPixels).roundToInt()
        }
    }

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
                                MainBroadcastPreference.Action.PINCHING -> {
                                    intent.getBooleanExtra(MainBroadcastPreference.Extra.IsZoomIn.KEY, MainBroadcastPreference.Extra.IsZoomIn.PredefinedValues.ZOOM_IN).also {
                                        mImageListRecyclerAdapter.resizeOnPinch(it) {
                                            setRecyclerViewLayoutManager()
                                        }
                                    }
                                }

                                // Pinch 중에는 Refresh 비활성화
                                MainBroadcastPreference.Action.PINCH_STATE -> {
                                    when(intent.getBooleanExtra(MainBroadcastPreference.Extra.IsPichBeigin.KEY, MainBroadcastPreference.Extra.IsPichBeigin.PredefinedValues.END)) {
                                        MainBroadcastPreference.Extra.IsPichBeigin.PredefinedValues.BEGIN -> mViewDataBinding.imageListRefreshLayout.isEnabled = false
                                        MainBroadcastPreference.Extra.IsPichBeigin.PredefinedValues.END -> {
                                            if(!mImageListViewModel.mFilterMenuVisibility.get()!!) {
                                                Handler().postDelayed({
                                                    mViewDataBinding.imageListRefreshLayout.isEnabled = true
                                                }, 300)
                                            }
                                        }
                                    }
                                }

                                // 이미지 선택 모드(단일 & 다중) 변경 알림
                                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED -> {
                                    with(intent.getBooleanExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY, true)) {
                                        mImageListRecyclerAdapter.setSelectionMode(this)
                                        if(this) showFilterMenu()
                                        else hideFilterMenu()
                                    }
                                }

                                // 백 버튼 눌림
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    if(mImageListViewModel.mPageNumber > 1) mImageListViewModel.boundOnPrevPageButtonClick()
                                    else mActivity?.sendBroadcast(Intent().apply {
                                        action = MainBroadcastPreference.Action.FINISH_APPLICATION
                                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY)
                                    })
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_image_list
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ImageListViewModel = mImageListViewModel

    override fun setUp() {
        setBroadcastReceiver()
        setImageListRecyclerAdapter()
        setViewModelListener()
        setRefreshLayout()
        setFilterMenu()
    }

    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mImageListBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT,
                MainBroadcastPreference.Action.SORT_OPTION_CHANGED,
                MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED,
                MainBroadcastPreference.Action.PINCHING,
                MainBroadcastPreference.Action.PINCH_STATE,
                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED,
                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
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
            val position = if(layoutManager != null) (layoutManager as GridLayoutManager).findFirstVisibleItemPosition() else 0
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, portraitImageColumnCount)
            else GridLayoutManager(mActivity, portraitImageColumnCount * mColumnCountRatio)
            scrollToPosition(position)
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
                    with(mViewDataBinding.imageListRefreshLayout) {
                        isEnabled = true
                        if(isRefreshing) isRefreshing = false
                    }
                }
            }
        }
    }

    private fun setRefreshLayout() {
        mViewDataBinding.imageListRefreshLayout.apply {
            setWaveRGBColor(255, 237, 163)
            isEnabled = false
            setOnRefreshListener {
                mViewDataBinding.imageListRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_alpha_disappear).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) = Unit
                        override fun onAnimationEnd(p0: Animation?) {
                            mImageListRecyclerAdapter.clear()
                            mImageListViewModel.refresh()
                        }
                        override fun onAnimationStart(p0: Animation?) = Unit
                    })
                })
            }
        }
    }

    private fun setFilterMenu() {
        val downloadButton = 0
        val shareButton = 1
        FilterMenu.Builder(context)
            .addItem(R.drawable.ic_download)
            .addItem(R.drawable.ic_share)
            .attach(mViewDataBinding.imageListFilterMenu)
            .withListener(object : FilterMenu.OnMenuChangeListener {
                override fun onMenuItemClick(view: View?, position: Int) {
                    when(position) {
                        downloadButton -> mImageOperationController.startDownload()
                        shareButton -> mImageOperationController.startShare()
                    }
                    hideFilterMenu()
                    mImageOperationController.clearImageModels()
                    mImageListRecyclerAdapter.setSelectionMode(false)
                }
                override fun onMenuCollapse() = Unit
                override fun onMenuExpand() = Unit
            })
            .build()
    }

    private fun showFilterMenu() {
        mImageListViewModel.showFilterMenu()
        val filterAppearAnim = AnimationUtils.loadAnimation(context, R.anim.anim_filter_menu_appear)
        mViewDataBinding.imageListFilterMenu.startAnimation(filterAppearAnim)
        mViewDataBinding.imageListRefreshLayout.isEnabled = false
    }

    private fun hideFilterMenu() {
        if(mImageListViewModel.mFilterMenuVisibility.get()!!) {
            val filterDisappearAnim = AnimationUtils.loadAnimation(context, R.anim.anim_filter_menu_disappear)
            filterDisappearAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(p0: Animation?) {
                    mImageListViewModel.hideFilterMenu()
                }
                override fun onAnimationRepeat(p0: Animation?) = Unit
                override fun onAnimationStart(p0: Animation?) = Unit
            })
            mViewDataBinding.imageListFilterMenu.startAnimation(filterDisappearAnim)
            mViewDataBinding.imageListRefreshLayout.isEnabled = true
        }
    }

    companion object {
        fun newInstance() = ImageListFragment()
    }

}






















package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

/**
 * 검색한 이미지들을 Grid 로 뿌려주는 프래그먼트입니다. 페이지간 이동 버튼과
 * Recycler View 등으로 구성되어 있습니다.
 *
 * @property mImageListViewModel 이미지 리스트 프래그먼트의 뷰 모델입니다.
 * @property mImageListRecyclerAdapter 이미지 리스트를 보여주는 Recycler View 의 어댑터입니다.
 * @property mPreferenceUtils 사용자 설정 정보 Utility 객체입니다.
 * @property mImageOperationController 이미지 공유/다운로드 제어기 객체입니다.
 * @property mColumnCountRatio 사용자 스마트의 가로/세로 비율에 맞춰 Recycler Grid 열의 갯수 비율을 정리합니다.
 *                              가령, 세로와 가로의 비율이 1:2 일 경우 이 값은 2가 되며 가로 모드일 때 열의 갯수는 2배가 됩니다.
 * @property mImageListBroadcastReceiver 이미지 리스트 프래그먼트에서 사용하는 방송 수신자입니다.
 * @property mRefreshDisableHandler Pinch 종료 후 Refresh 를 다시 활성화 할 때 지연시간을 담아 처리해주는 핸들러입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
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
    private val mRefreshDisableHandler = Handler()
    private val mImageListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                    actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                        target ->
                        if(target == MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST) {
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
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingOperation.KEY)) {
                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_IN -> mImageListRecyclerAdapter.resizeOnPinch(true) { setRecyclerViewLayoutManager() }
                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_OUT -> mImageListRecyclerAdapter.resizeOnPinch(false) { setRecyclerViewLayoutManager() }
                                    }
                                }

                                // Pinch 중에는 Refresh 비활성화
                                MainBroadcastPreference.Action.PINCH_STATE -> {
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingState.KEY)) {
                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_START -> mViewDataBinding.imageListRefreshLayout.isEnabled = false
                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_END -> {
                                            if(!mImageListViewModel.mFilterMenuVisibility.get()!!) {
                                                mRefreshDisableHandler.removeCallbacksAndMessages(null)
                                                mRefreshDisableHandler.postDelayed({
                                                    mViewDataBinding.imageListRefreshLayout.isEnabled = true
                                                }, 300)
                                            }
                                        }
                                    }
                                }

                                // 이미지 선택 모드(단일 & 다중) 변경 알림
                                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED -> {
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY)) {
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.MULTI_SELECTION_MODE -> {
                                            mImageListRecyclerAdapter.setSelectionMode(true)
                                            showFilterMenuWithAnimation()
                                        }
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.SIGNLE_SELECTION_MODE -> {
                                            mImageListRecyclerAdapter.setSelectionMode(false)
                                            hideFilterMenuWithAnimation()
                                        }
                                    }
                                }

                                // 백 버튼 눌림
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    if(mImageListViewModel.mPageNumber > 1) mImageListViewModel.boundOnPrevPageButtonClick()
                                    else mActivity?.sendBroadcast(Intent().apply {
                                        action = MainBroadcastPreference.Action.FINISH_APPLICATION
                                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
                                    })
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    // BaseFragment 에서 상속받아 사용하는 기본적인 메소드입니다.
    override fun getLayoutId(): Int = R.layout.fragment_image_list
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ImageListViewModel = mImageListViewModel




    // 전체 프래그먼트 설정 순서입니다. onViewCreated 에서 실행됩니다.
    override fun setUp() {
        setBroadcastReceiver()
        setImageListRecyclerAdapter()
        setViewModelListener()
        setRefreshLayout()
        setFilterMenu()
    }




    /** 방송 수신자를 등록합니다. */
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

    /**
     * Fragment 생명주기 onDestroy 에 다음의 내용을 실행합니다.
     * 앞서 등록한 방송수신자를 제거합니다.
     */
    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mImageListBroadcastReceiver)
        mImageListRecyclerAdapter.clear()
    }




    /** RecyclerView 에 어댑터를 등록해줍니다. */
    private fun setImageListRecyclerAdapter() {
       mViewDataBinding.imageListRecyclerView.apply {
           adapter = mImageListRecyclerAdapter
       }
        setRecyclerViewLayoutManager()
    }

    /**
     * RecyclerView 에 LayoutManager 를 등록해줍니다.
     * LayoutManager 는 기본적으로 GridLayoutManager 를 사용하며
     * 현재 칼럼의 갯수와 화면 방향, 비율 등을 고려하여 Dynamic 하게 변경됩니다.
     */
    private fun setRecyclerViewLayoutManager() {
        val portraitImageColumnCount = mPreferenceUtils.getImageColumnCount()
        mViewDataBinding.imageListRecyclerView.apply {
            val position = if(layoutManager != null) (layoutManager as GridLayoutManager).findFirstVisibleItemPosition() else 0
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, portraitImageColumnCount)
            else GridLayoutManager(mActivity, portraitImageColumnCount * mColumnCountRatio)
            scrollToPosition(position)
        }
    }

    /** Fragment onConfigurationChanged 에 다음의 내용을 실행합니다.
     * setRecyclerViewLayoutManager 를 다시 한 번 호출합니다.
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager()
    }



    /**
     * View Model 에서 추가적으로 지정해줘야 하는 리스너들을 등록합니다.
     * 검색어가 변경되었을 때 Recycler View 에 새로운 검색 내용을 전달하고
     * 그 결과를 다시 View Model 에 반환합니다.
     */
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


    /**
     * 사설 Refresh Layout 의 세부 사항을 설정합니다.
     * Refresh 가 시작될 때 Recycler View 를 Alpha 애니메이션과 함께
     * 제거하고 Recycler View 를 Clear 합니다. 이후 현재 View Model 에
     * Refresh 를 요청합니다.
     */
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


    /**
     * 사설 Filter Menu 의 세부 사항을 설정합니다.
     * 다운로드 버튼은 0, 공유 버튼을 1로 지정하고 각각의 아이콘을 설정하며
     * 클릭 시의 이벤트에 따라 이미지 공유/다운로드 관리자에 메시지를 전달합니다.
     */
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
                    hideFilterMenuWithAnimation()
                    mImageOperationController.clearImageModels()
                    mImageListRecyclerAdapter.setSelectionMode(false)
                }
                override fun onMenuCollapse() = Unit
                override fun onMenuExpand() = Unit
            })
            .build()
    }

    /** Filter Menu 를 Translation 애니메이션과 함께 화면에 띄웁니다. */
    private fun showFilterMenuWithAnimation() {
        mImageListViewModel.showFilterMenu()
        val filterAppearAnim = AnimationUtils.loadAnimation(context, R.anim.anim_filter_menu_appear)
        mViewDataBinding.imageListFilterMenu.startAnimation(filterAppearAnim)
        mViewDataBinding.imageListRefreshLayout.isEnabled = false
    }

    /** Filter Menu 를 Translation 애니메이션과 함께 화면에서 제거합니다. */
    private fun hideFilterMenuWithAnimation() {
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
        /** 새로운 프래그먼트를 생성합니다. */
        fun newInstance() = ImageListFragment()
    }

}






















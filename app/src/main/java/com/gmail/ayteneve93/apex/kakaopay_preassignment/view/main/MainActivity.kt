package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Handler
import android.provider.SearchRecentSuggestions
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.FragmentManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.KakaoImageModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ActivityMainBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseActivity
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_detail.ImageDetailFragment
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.ImageListFragment
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * 애플리케이션의 중심이 되는 MainActivity 입니다. 툴 바와 프래그먼트, 기본적인
 * Progress Indicator 등을 가지며 전체 앱을 통제하는 기능을 수행합니다.
 *
 * @property mMainViewModel 메인 액티비티의 뷰 모델입니다.
 * @property mPreferenceUtils 사용자 설정 정보 Utility 객체입니다.
 * @property mImageOperationController 이미지 공유/다운로드 제어기 객체입니다.
 * @property mAppTerminateConfirmHandler 애플리케이션 종료를 시간차를 두고 진행하기 위핸 Handler 객체입니다.
 * @property mImageDownloadCompleteNotificationId 이미지 다운로드 완료 Notification 의 Id 입니다.
 * @property mImageDownloadCompleteNotificationReqCode 이미지 다운로드 완료 Notification 의 요청 코드입니다.
 * @property mImageDownloadCompleteNotificationChannelId 이미지 다운로드 완료 Notification 의 채널 Id 입니다.
 * @property mImageDownloadCompleteNotificationChannelName 이미지 다운로드 완료 Notification 의 채널 명입니다.
 * @property mBackButtonEnabledFromDetail 이미지 상세정보 프래그먼트에서 BackButton 을 허용하는지 여부를 지정하는 Boolean 입니다.
 * @property mIsOnMultipleSelectionMode 현재 이미지 선택 모드가 다중 선택 모드인지 확인하는 Boolean 입니다.
 * @property mAppTerminateConfirmFlag 애플리케이션 종료 확인 요청이 있었는지 확인하는 Boolean 입니다.
 * @property mIsSearchViewShownAtFirstTime 처음 앱 시작시 서치 뷰에 focus 가 가해졌는지 확인하는 Boolean 입니다.
 * @property mSearchView 이미지 검색 쿼리를 관리하는 서치 뷰입니다.
 * @property mScaleGestureDetector Pinch 이벤트를 처리하는 디텍터 객체입니다.
 * @property mFragmentManager 프래그먼트 전환을 관리하는 객체입니다.
 * @property mMainFragmentState 현재 프래그먼트 상태입니다.
 * @property mImageListFragment 이미지 리스트 프래그먼트입니다.
 * @property mNotificationManager Notification 수행 객체입니다.
 * @property mMainBroadcastReceiver 메인 액티비티에서 사용하는 방송 수신자입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel : MainViewModel by viewModel()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private val mImageOperationController : ImageOperationController by inject()
    private val mAppTerminateConfirmHandler = Handler()
    private val mImageDownloadCompleteNotificationId = 0
    private val mImageDownloadCompleteNotificationReqCode = 1000
    private val mImageDownloadCompleteNotificationChannelId = "view.main.mImageDownloadCompleteNotificationChannelId"
    private val mImageDownloadCompleteNotificationChannelName = "view.main.mImageDownloadCompleteNotificationChannelName"

    private var mBackButtonEnabledFromDetail = true
    private var mIsOnMultipleSelectionMode = false
    private var mAppTerminateConfirmFlag = false
    private var mIsSearchViewShownAtFirstTime = false

    private lateinit var mSearchView : SearchView
    private lateinit var mScaleGestureDetector : ScaleGestureDetector
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mMainFragmentState: MainFragmentState
    private lateinit var mImageListFragment: ImageListFragment
    private lateinit var mNotificationManager : NotificationManager

    private val mMainBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                    actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                        target ->
                        if(target == MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY) {
                            when(actionString) {

                                // 이미지 아이템이 클릭된 경우
                                MainBroadcastPreference.Action.IMAGE_ITEM_CLICKED -> {
                                    showImageDetailFragment(intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageItem.KEY) as KakaoImageModel)
                                }

                                // 이미지 상세정보 프래그먼트를 강제 종료하라는 명령
                                MainBroadcastPreference.Action.CLOSE_IMAGE_DETAIL_FRAGMENT -> {
                                    mMainFragmentState = MainFragmentState.IMAGE_LIST
                                    super@MainActivity.onBackPressed()
                                }

                                // 이미지 선택 모드(단일 & 다중) 변경 알림
                                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED -> {
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY)) {
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.MULTI_SELECTION_MODE -> mIsOnMultipleSelectionMode = true
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.SIGNLE_SELECTION_MODE -> mIsOnMultipleSelectionMode = false
                                    }
                                }

                                // 이미지 공유/다운로드 절차가 완료된 경우
                                MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED -> {
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageOperation.KEY)) {
                                        MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.SHARE -> {
                                            this@MainActivity.startActivity(
                                                Intent.createChooser(intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT), getString(R.string.intent_title_image_sharing_target)))
                                        }
                                        MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.DOWNLOAD -> {
                                            Toast.makeText(this@MainActivity, R.string.txt_image_download_succeed, Toast.LENGTH_LONG).show()
                                            showImageDownloadCompleteNotification()
                                            mImageOperationController.clearDisposable()
                                        }
                                    }
                                }

                                // 애플리케이션 종료 명령
                                MainBroadcastPreference.Action.FINISH_APPLICATION -> {
                                    finishApplication()
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    // BaseActivity 에서 상속받아 사용하는 기본적인 메소드입니다.
    override fun getLayoutId(): Int = R.layout.activity_main
    override fun getViewModel(): MainViewModel = mMainViewModel
    override fun getBindingVariable(): Int = BR.viewModel



    // 전체 액티비티 설정 순서입니다. onCreate 에서 실행됩니다.
    override fun setUp() {
        setBroadcastReceiver()
        setToolBar()
        setScaleGestureDetector()
        setFragmentManager()
        setNotificationChannel()
        setImageOperationIndicator()
    }

    /**
     * Activity 생명주기 onResume 에 다음의 내용을 실행합니다.
     * 이미지 공유/다운로드 제어 객체에 임시 공유 파일과 Disposable 을 제거하라고 명령합니다.
     */
    override fun onResume() {
        super.onResume()
        mImageOperationController.clearSharedDriectory()
    }



    /** 방송 수신자를 등록합니다. */
    private fun setBroadcastReceiver() {
        registerReceiver(mMainBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.IMAGE_ITEM_CLICKED,
                MainBroadcastPreference.Action.CLOSE_IMAGE_DETAIL_FRAGMENT,
                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED,
                MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED,
                MainBroadcastPreference.Action.FINISH_APPLICATION
            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    /**
     * Activity 생명주기 onDestroy 에 다음의 내용을 실행합니다.
     * 앞서 등록한 방송수신자를 제거합니다.
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
    }




    /**
     * 앱 툴 바를 사설 Layout Component 로 교체합니다.
     * 또한 아이콘은 앱 런쳐로, 타이틀은 개발자 이름으로 지정합니다.
     */
    private fun setToolBar() {
        setSupportActionBar(mViewDataBinding.mainToolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setIcon(R.drawable.ic_image_finder_mini)
            it.title = getString(R.string.developer_name)
        }
    }

    /**
     * Activity onCreateOptionsMenu 에 다음의 내용을 실행합니다.
     * 서치 뷰를 Inflate 하고 리스너를 등록합니다. 또한 제출된 Query 를
     * Suggestion 에 저장하고 ImageList Fragment 에 전달합니다.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_app_bar, menu)
        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val suggestions = SearchRecentSuggestions(this, MainRecentSearchSuggestionsProvider.AUTHORITY,
            MainRecentSearchSuggestionsProvider.MODE)

        mSearchView = menu!!.findItem(R.id.menuMainAppBarSearch).actionView as SearchView
        mSearchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.search_view_hint)
            setOnQueryTextFocusChangeListener {
                _, isFocused ->
                mMainViewModel.mFragmentVisibility.set(!isFocused)
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow((currentFocus?: View(this@MainActivity)).windowToken, 0)
                    this@apply.onActionViewCollapsed()
                    if(mMainFragmentState == MainFragmentState.IMAGE_DETAIL) onBackPressed()
                    sendBroadcast(Intent().apply {
                        action = MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT
                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                        putExtra(MainBroadcastPreference.Extra.QueryString.KEY, query)
                    })
                    dismissMultiSelectionMode()
                    suggestions.saveRecentQuery(query, null)
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean = newText?.isNotEmpty()?:false
            })
            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean = true
                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = suggestionsAdapter.getItem(position) as Cursor
                    val index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)
                    setQuery(cursor.getString(index), true)
                    return true
                }
            })
            setOnCloseListener { false }
            isQueryRefinementEnabled = true
            if(!mIsSearchViewShownAtFirstTime) {
                isIconified = false
                requestFocus(0)
                mIsSearchViewShownAtFirstTime = true
            }
        }
        return true
    }

    /**
     * Activity onPrepareOptionsMenu 에 다음의 내용을 실행합니다.
     * PreferenceUtils 로 부터 환경설정 정보를 읽어와
     * 정렬 기준과 표시 갯수를 설정합니다.
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when(mPreferenceUtils.getSortOption()) {
            KakaoImageSortOption.ACCURACY -> menu!!.findItem(R.id.menuMainAppBarSortByAccuracy).isChecked = true
            KakaoImageSortOption.RECENCY -> menu!!.findItem(R.id.menuMainAppBarSortByRecency).isChecked = true
        }
        when(mPreferenceUtils.getDisplayCount()) {
            30 -> menu.findItem(R.id.menuMainAppBarDisplayCount_30).isChecked = true
            50 -> menu.findItem(R.id.menuMainAppBarDisplayCount_50).isChecked = true
            80 -> menu.findItem(R.id.menuMainAppBarDisplayCount_80).isChecked = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Activity onOptionsItemSelected 에 다음의 내용을 실행합니다.
     * 선택된 Id 값을 기반으로 Image List 프래그먼트에 그에 맞는
     * 정보를 Broadcast 로 전달합니다.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuMainAppBarSortByAccuracy -> {
                mPreferenceUtils.setSortOption(KakaoImageSortOption.ACCURACY)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoImageSortOption.ACCURACY)
                })
            }
            R.id.menuMainAppBarSortByRecency -> {
                mPreferenceUtils.setSortOption(KakaoImageSortOption.RECENCY)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoImageSortOption.RECENCY)
                })
            }
            R.id.menuMainAppBarDisplayCount_30 -> {
                mPreferenceUtils.setDisplayCount(30)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 30)
                })
            }
            R.id.menuMainAppBarDisplayCount_50 -> {
                mPreferenceUtils.setDisplayCount(50)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 50)
                })
            }
            R.id.menuMainAppBarDisplayCount_80 -> {
                mPreferenceUtils.setDisplayCount(80)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 80)
                })
            }
            R.id.menuMainAppBarClearSearchHistory -> {
                SearchRecentSuggestions(this, MainRecentSearchSuggestionsProvider.AUTHORITY,
                    MainRecentSearchSuggestionsProvider.MODE).clearHistory()
            }
        }
        dismissMultiSelectionMode()
        if(mMainFragmentState == MainFragmentState.IMAGE_DETAIL) onBackPressed()
        return false
    }




    /**
     * 사설 제스쳐 디텍터인 ScaleGestureDetector 를 선언합니다.
     * ScaleGestureDetector 는 사용자가 화면을 Pinch 한 이벤트를
     * Catch 하여 Image List 프래그먼트로 전달합니다. 단, 현재 프래그먼트가
     * Image Detail 인 경우에는 전달하지 않습니다.
     */
    private fun setScaleGestureDetector() {
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private val mScaleSensitivity = 7
            private val mStandardScaleFactor = 1.0f
            /** Pinch 제스쳐 이벤트가 감지되면 Zoom In 이벤트 인지 Zomm Out 이벤트인지 구분해서 ImageList 프래그먼트에 전달합니다. */
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                if(mMainFragmentState == MainFragmentState.IMAGE_LIST) {
                    detector?.let {
                        if (kotlin.math.abs(it.currentSpan - it.previousSpan) > mScaleSensitivity) {
                            sendBroadcast(Intent().apply {
                                action = MainBroadcastPreference.Action.PINCHING
                                putExtra(
                                    MainBroadcastPreference.Target.KEY,
                                    MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST
                                )
                                putExtra(MainBroadcastPreference.Extra.PinchingOperation.KEY,
                                    if(it.scaleFactor > mStandardScaleFactor) MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_IN
                                    else MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_OUT)
                            })
                        }
                    }
                }
                return true
            }

            /** Pinch 제스쳐 이벤트가 시작될 때 해당 사항을 ImageList 프래그먼트에 전달합니다. */
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.PINCH_STATE
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST
                    )
                    putExtra(
                        MainBroadcastPreference.Extra.PinchingState.KEY,
                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_START)
                })
                return super.onScaleBegin(detector)
            }

            /** Pinch 제스쳐 이벤트가 종료될 때 해당 사항을 ImageList 프래그먼트에 전달합니다. */
            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.PINCH_STATE
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST
                    )
                    putExtra(
                        MainBroadcastPreference.Extra.PinchingState.KEY,
                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_END)
                })
                super.onScaleEnd(detector)
            }
        })
    }

    /**
     * Activity dispatchTouchEvent 에 다음의 내용을 실행합니다.
     * 사설 제스쳐 디텍터가 정의된 경우 디텍터에 터치 이벤트를 전달합니다.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(::mScaleGestureDetector.isInitialized) mScaleGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }


    /**
     * Activity onBackPressed 에 다음의 내용을 실행합니다.
     * 1. 현재 선택 모드가 다중 모드일 경우 -> 단일 모드로 변경
     * 2. Search View 가 활성화 된 경우 -> Search View Collapse
     * 3. 현재 프래그먼트 상태가 ImageList 인 경우 -> 해당 프래그먼트에 이벤트 전달
     * 4. 현재 프래그먼트 상태가 ImageDetail 인 경우 -> 해당 프래그먼트에 이벤트 전달
     */
    override fun onBackPressed() {
        if(mIsOnMultipleSelectionMode) {
            dismissMultiSelectionMode()
            return
        }

        if(!mSearchView.isIconified) {
            mSearchView.onActionViewCollapsed()
            return
        }

        if(mMainFragmentState == MainFragmentState.IMAGE_LIST){
            if(mBackButtonEnabledFromDetail) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST
                    )
                })
            }
            return
        }

        if(mMainFragmentState == MainFragmentState.IMAGE_DETAIL){
            if(mBackButtonEnabledFromDetail) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.IMAGE_DETAIL
                    )
                })
            }
            return
        }
    }

    /**
     * 애플리케이션을 종료하는 메소드입니다. 단, 뒤로가기 버튼을 눌렀다고 바로
     * 종료되지는 않고, 3초 정도 대기시간을 부여한 후에 한 번 더 눌릴 경우 종료합니다.
     */
    private fun finishApplication() {
        if(!mAppTerminateConfirmFlag) {
            Toast.makeText(this, R.string.press_again_to_exist, Toast.LENGTH_LONG).show()
            mAppTerminateConfirmFlag = true
            mAppTerminateConfirmHandler.removeCallbacksAndMessages(null)
            mAppTerminateConfirmHandler.postDelayed({
                mAppTerminateConfirmFlag = false
            }, 3000)
            return
        }
        finish()
    }

    /** 다중 선택 모드를 해제합니다. */
    private fun dismissMultiSelectionMode() {
        if(mIsOnMultipleSelectionMode) {
            sendBroadcast(Intent().apply {
                action = MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED
                putExtra(
                    MainBroadcastPreference.Target.KEY,
                    MainBroadcastPreference.Target.PreDefinedValues.IMAGE_LIST
                )
                putExtra(
                    MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY,
                    MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.SIGNLE_SELECTION_MODE
                )
            })
            mIsOnMultipleSelectionMode = false
        }
    }


    /**
     * 프래그먼트 매니져를 설정합니다. ImageList 프래그먼트는 애플리케이션 특성상
     * 여러개가 생성될 일이 없으므로 Activity 의 Property 로 지정하고 바로 Show 해줍니다.
     */
    private fun setFragmentManager() {
        mMainFragmentState = MainFragmentState.IMAGE_LIST
        mFragmentManager = supportFragmentManager
        mImageListFragment = ImageListFragment.newInstance()
        mFragmentManager
            .beginTransaction()
            .add(mViewDataBinding.mainFragmentContainer.id, mImageListFragment)
            .show(mImageListFragment)
            .commit()
    }

    /**
     * 프래그먼트를 Image Detail 로 변경합니다.
     * 이 때는 Image Detail 프래그먼트를 새로이 생성하고 화면을 전환합니다.
     */
    private fun showImageDetailFragment(imageModel : KakaoImageModel) {
        mMainFragmentState = MainFragmentState.IMAGE_DETAIL
        val imageDetailFragment = ImageDetailFragment.newInstance(application, imageModel, mImageOperationController)
        mFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left,
                R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right)
            .hide(mImageListFragment)
            .add(mViewDataBinding.mainFragmentContainer.id, imageDetailFragment)
            .show(imageDetailFragment)
            .addToBackStack(null)
            .commit()
        mBackButtonEnabledFromDetail = false
        Handler().postDelayed({mBackButtonEnabledFromDetail = true}, 500)
    }


    /**
     * 알림 채널을 설정합니다.
     */
    private fun setNotificationChannel() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val imageDownloadCompleteNotificationChannel : NotificationChannel = NotificationChannel (
            mImageDownloadCompleteNotificationChannelId,
            mImageDownloadCompleteNotificationChannelName,
            NotificationManager.IMPORTANCE_LOW).apply {
            enableLights(false)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        mNotificationManager.createNotificationChannel(imageDownloadCompleteNotificationChannel)
    }

    /**
     * 다운로드가 완료된 경우 해당 채널에 Notification 을 보냅니다.
     */
    private fun showImageDownloadCompleteNotification() {
        val openGalleryIntent : Intent = Intent().apply {
            action = Intent.ACTION_VIEW
            type = "image/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val imageDownloadCompletePendingIntent : PendingIntent = PendingIntent.getActivity(
            this, mImageDownloadCompleteNotificationReqCode,
            openGalleryIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val imageDownloadCompleteNotification : Notification = Notification.Builder(this, mImageDownloadCompleteNotificationChannelId)
            .setContentTitle(getString(R.string.txt_image_download_succeed))
            .setContentText(getString(R.string.txt_image_download_succeed_notification_message))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
            .setContentIntent(imageDownloadCompletePendingIntent)
            .build()
        mNotificationManager.notify(mImageDownloadCompleteNotificationId, imageDownloadCompleteNotification)
    }




    /**
     * 이미지 파일이 다운 혹은 공유 작업 중일 때 Indicator 를 보여줍니다.
     */
    private fun setImageOperationIndicator() {
        mViewDataBinding.mainImageOperationIndicator.bringToFront()
        mImageOperationController.mIsOnOperation.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(mImageOperationController.mIsOnOperation.get()!!) {
                    mMainViewModel.mProgressIndicatorVisibility.set(true)
                    mViewDataBinding.mainImageOperationIndicator.showProgressBar()
                }
                else {
                    mMainViewModel.mProgressIndicatorVisibility.set(false)
                    mViewDataBinding.mainImageOperationIndicator.hideProgressBar()
                }
            }
        })
    }

}
























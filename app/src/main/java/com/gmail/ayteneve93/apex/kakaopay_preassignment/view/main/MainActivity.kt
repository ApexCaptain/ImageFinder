package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
                        if(target == MainBroadcastPreference.Target.PredefinedValues.MAIN_ACTIVITY) {
                            when(actionString) {

                                // 이미지 아이템이 클릭됨
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
                                    when(intent.getBooleanExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY, true)) {
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PredefinedValues.SELECTION_MODE -> {
                                            mIsOnMultipleSelectionMode = true

                                        }
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PredefinedValues.NORMAL_MODE -> {
                                            mIsOnMultipleSelectionMode = false
                                        }
                                    }
                                }

                                MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED -> {
                                    when(intent.getStringExtra(MainBroadcastPreference.Extra.ImageOperation.KEY)) {
                                        MainBroadcastPreference.Extra.ImageOperation.PredefinedValues.SHARE -> {
                                            this@MainActivity.startActivity(
                                                Intent.createChooser(intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT), getString(R.string.intent_title_image_sharing_target)))
                                        }
                                        MainBroadcastPreference.Extra.ImageOperation.PredefinedValues.DOWNLOAD -> {
                                            Toast.makeText(this@MainActivity, R.string.txt_image_download_succeed, Toast.LENGTH_LONG).show()
                                            showImageDownloadCompleteNotification()
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

    override fun getLayoutId(): Int = R.layout.activity_main
    override fun getViewModel(): MainViewModel = mMainViewModel
    override fun getBindingVariable(): Int = BR.viewModel

    override fun setUp() {
        setBroadcastReceiver()
        setToolBar()
        setScaleGestureDetector()
        setFragmentManager()
        setNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        mImageOperationController.clearSharedDriectory()
    }

    private fun setBroadcastReceiver() {
        registerReceiver(mMainBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.IMAGE_ITEM_CLICKED,
                MainBroadcastPreference.Action.CLOSE_IMAGE_DETAIL_FRAGMENT,
                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED,
                MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED
            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
    }

    private fun setToolBar() {
        setSupportActionBar(mViewDataBinding.mainToolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setIcon(R.drawable.ic_image_finder_mini)
            it.title = getString(R.string.developer_name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_app_bar, menu)
        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                        putExtra(MainBroadcastPreference.Extra.QueryString.KEY, query)
                    })
                    dismissMultiSelectionMode()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean = true
            })
            setOnCloseListener {
                false
            }
        }
        return true
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuMainAppBarSortByAccuracy -> {
                mPreferenceUtils.setSortOption(KakaoImageSortOption.ACCURACY)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoImageSortOption.ACCURACY)
                })
            }
            R.id.menuMainAppBarSortByRecency -> {
                mPreferenceUtils.setSortOption(KakaoImageSortOption.RECENCY)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoImageSortOption.RECENCY)
                })
            }
            R.id.menuMainAppBarDisplayCount_30 -> {
                mPreferenceUtils.setDisplayCount(30)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 30)
                })
            }
            R.id.menuMainAppBarDisplayCount_50 -> {
                mPreferenceUtils.setDisplayCount(50)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 50)
                })
            }
            R.id.menuMainAppBarDisplayCount_80 -> {
                mPreferenceUtils.setDisplayCount(80)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                    putExtra(MainBroadcastPreference.Extra.DisplayCount.KEY, 80)
                })
            }
        }
        dismissMultiSelectionMode()
        if(mMainFragmentState == MainFragmentState.IMAGE_DETAIL) onBackPressed()
        return false
    }

    private fun setScaleGestureDetector() {
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private val scaleSensitivity = 7
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                if(mMainFragmentState == MainFragmentState.IMAGE_LIST) {
                    detector?.let {
                        if (kotlin.math.abs(it.currentSpan - it.previousSpan) > scaleSensitivity) {
                            sendBroadcast(Intent().apply {
                                action = MainBroadcastPreference.Action.PINCHING
                                putExtra(
                                    MainBroadcastPreference.Target.KEY,
                                    MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST
                                )
                                putExtra(MainBroadcastPreference.Extra.IsZoomIn.KEY, it.scaleFactor > 1)
                            })
                        }
                    }
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.PINCH_STATE
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST
                    )
                    putExtra(
                        MainBroadcastPreference.Extra.IsPichBeigin.KEY,
                        MainBroadcastPreference.Extra.IsPichBeigin.PredefinedValues.BEGIN)
                })
                return super.onScaleBegin(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.PINCH_STATE
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST
                    )
                    putExtra(
                        MainBroadcastPreference.Extra.IsPichBeigin.KEY,
                        MainBroadcastPreference.Extra.IsPichBeigin.PredefinedValues.END)
                })
                super.onScaleEnd(detector)
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(::mScaleGestureDetector.isInitialized) mScaleGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if(mIsOnMultipleSelectionMode) {
            dismissMultiSelectionMode()
            return
        }

        if(!mSearchView.isIconified) {
            mSearchView.onActionViewCollapsed()
            return
        }

        if(mMainFragmentState == MainFragmentState.IMAGE_DETAIL){
            if(mBackButtonEnabledFromDetail) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PredefinedValues.IMAGE_DETAIL
                    )
                })
            }
            return
        }

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

    private fun dismissMultiSelectionMode() {
        if(mIsOnMultipleSelectionMode) {
            sendBroadcast(Intent().apply {
                action = MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED
                putExtra(
                    MainBroadcastPreference.Target.KEY,
                    MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST
                )
                putExtra(
                    MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY,
                    MainBroadcastPreference.Extra.ImageItemSelectionMode.PredefinedValues.NORMAL_MODE
                )
            })
            mIsOnMultipleSelectionMode = false
        }
    }

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

}
























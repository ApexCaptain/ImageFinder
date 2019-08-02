package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.library.baseAdapters.BR
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ActivityMainBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel : MainViewModel by viewModel()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private lateinit var mSearchView : SearchView
    private lateinit var mScaleGestureDetector : ScaleGestureDetector


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): MainViewModel {
        return mMainViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun setUp() {
        setToolBar()
        setScaleGestureDetector()
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
                    // ToDo 여기서 현재 Frag 상태 확인하고 Detail 이면 Search 로 바꾸는 코드 들어가야함
                    sendBroadcast(Intent().apply {
                        action = MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT
                        putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                        putExtra(MainBroadcastPreference.Extra.QueryString.KEY, query)
                    })
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
        }
        return false
    }

    private fun setScaleGestureDetector() {
        mScaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private val scaleSensitivity = 7
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                detector?.let {
                    if(kotlin.math.abs(it.currentSpan - it.previousSpan) > scaleSensitivity) {
                        sendBroadcast(Intent().apply {
                            action = MainBroadcastPreference.Action.PINCH
                            putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                            putExtra(MainBroadcastPreference.Extra.PinchState.KEY, it.scaleFactor > 1)
                        })
                    }
                }
                return true
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(::mScaleGestureDetector.isInitialized) mScaleGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}

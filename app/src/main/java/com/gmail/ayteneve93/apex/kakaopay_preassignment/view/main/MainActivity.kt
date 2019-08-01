package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.databinding.library.baseAdapters.BR
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageSortOption
import com.gmail.ayteneve93.apex.kakaopay_preassignment.databinding.ActivityMainBinding
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseActivity
import io.reactivex.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.viewModel
import io.reactivex.android.schedulers.AndroidSchedulers

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mMainViewModel : MainViewModel by viewModel()
    private lateinit var mSearchView : SearchView

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
        mSearchView = menu!!.findItem(R.id.menu_main_app_bar_search).actionView as SearchView
        mSearchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.search_view_hint)
            setOnQueryTextFocusChangeListener {
                view, isFocused ->
                mMainViewModel.mFragmentVisibility.set(!isFocused)
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow((currentFocus?: View(this@MainActivity)).windowToken, 0)
                    this@apply.onActionViewCollapsed()
                    // ToDo 여기서 현재 Frag 상태 확인하고 Detail 이면 Search 로 바꾸는 코드 들어가야함
                    sendBroadcast(Intent(MainBroadcastPreference.Action.NewSearchQueryInput)
                        .putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PredefinedValues.IMAGE_LIST)
                        .putExtra(MainBroadcastPreference.Extra.QueryString.Key, query))
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
}

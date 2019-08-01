package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.util.Log
import android.view.Menu
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
        return true
    }
}

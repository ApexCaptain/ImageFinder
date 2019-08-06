package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.Application
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

/**
 * MainActivity 의 ViewModel 입니다.
 *
 * @property mFragmentVisibility 프래그먼트 가시성을 관리하는 Observable<Boolean> 필드입니다
 * @property mProgressIndicatorVisibility 앱이 어떠한 작업을 진행중임을 보여주는 Observable<Boolean> 필드입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
class MainViewModel(
    application : Application
) : BaseViewModel(application){
    val mFragmentVisibility = ObservableField(true)
    val mProgressIndicatorVisibility = ObservableField(false)
}
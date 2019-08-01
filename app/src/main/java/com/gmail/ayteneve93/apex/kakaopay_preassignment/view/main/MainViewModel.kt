package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main

import android.app.Application
import androidx.databinding.ObservableField
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

class MainViewModel(
    application : Application
) : BaseViewModel(application){
    val mFragmentVisibility = ObservableField(true)
}
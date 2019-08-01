package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base.BaseViewModel

class ImageListViewModel(
    application: Application
) : BaseViewModel(application){
    var mTitle = MutableLiveData<CharSequence>().apply {
        value = application.getString(R.string.search_list_title_hint)
    }
}
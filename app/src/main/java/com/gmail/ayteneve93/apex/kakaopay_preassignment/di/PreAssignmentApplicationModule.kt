package com.gmail.ayteneve93.apex.kakaopay_preassignment.di

import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageDownloadController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.ImageListViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListItemViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        MainViewModel(get())
    }
    viewModel {
        ImageListViewModel(get(), get())
    }
    viewModel {
        ImageListItemViewModel(get(), get())
    }
}

val recyclerAdapter = module {
    single { ImageListRecyclerAdapter(get(), get(), get(), get()) }
}

val dataModelManager = module {
    single { KakaoImageModelManager() }
    single { ImageDownloadController(get()) }
}

val util = module {
    single { PreferenceUtils(get()) }
}

val PreAssignmentApplicationModule = listOf(
    viewModelModule,
    recyclerAdapter,
    dataModelManager,
    util
)
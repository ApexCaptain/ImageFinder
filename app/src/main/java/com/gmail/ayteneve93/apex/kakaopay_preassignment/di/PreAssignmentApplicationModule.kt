package com.gmail.ayteneve93.apex.kakaopay_preassignment.di

import com.gmail.ayteneve93.apex.kakaopay_preassignment.controller.ImageOperationController
import com.gmail.ayteneve93.apex.kakaopay_preassignment.data.manager.kakao_image_search.KakaoImageModelManager
import com.gmail.ayteneve93.apex.kakaopay_preassignment.utils.PreferenceUtils
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_detail.ImageDetailViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.ImageListViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListItemViewModel
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.fragments.image_list.recycler.ImageListRecyclerAdapter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** 뷰 모델 */
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
    viewModel {
        ImageDetailViewModel(get(), get(), get())
    }
}

/** 리사이클러 어댑터 */
val recyclerAdapter = module {
    single { ImageListRecyclerAdapter(get(), get(), get(), get()) }
}

/** 데이터 관리 모듈 */
val dataModelManager = module {
    single { KakaoImageModelManager() }
}

/** 컨트롤러 모듈 */
val controller = module {
    single { ImageOperationController(get()) }
}

/** 유틸리티 모듈 */
val util = module {
    single { PreferenceUtils(get()) }
}

/**
 * Kotlin Koin 의 DI 절차에 따라 구축한 모듈 리스트입니다
 */
val PreAssignmentApplicationModule = listOf(
    viewModelModule,
    recyclerAdapter,
    dataModelManager,
    controller,
    util
)
package com.gmail.ayteneve93.apex.kakaopay_preassignment.di

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
        ImageListViewModel(get())
    }
    viewModel {
        ImageListItemViewModel(get())
    }
}

val recyclerAdapter = module {
    single { ImageListRecyclerAdapter(get()) }
}

val PreAssignmentApplicationModule = listOf(
    viewModelModule,
    recyclerAdapter
)
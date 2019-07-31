package com.gmail.ayteneve93.apex.kakaopay_preassignment

import android.app.Application
import com.gmail.ayteneve93.apex.kakaopay_preassignment.di.PreAssignmentApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PreAssignmentApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        setDependencyInjection()
    }

    private fun setDependencyInjection() {
        startKoin {
            // DI - Android Context 를 Application 레벨에서 지정
            androidContext(this@PreAssignmentApplication)
            // DI - 앱 전체의 모듈들 정의
            modules(PreAssignmentApplicationModule)
        }
    }

}
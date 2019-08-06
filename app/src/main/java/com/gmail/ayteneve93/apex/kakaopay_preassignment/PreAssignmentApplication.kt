package com.gmail.ayteneve93.apex.kakaopay_preassignment

import android.app.Application
import com.gmail.ayteneve93.apex.kakaopay_preassignment.di.PreAssignmentApplicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * 본 안드로이드 프로젝트의 Application 클래스입니다.
 * 안드로이드 자체 런타임 플랫폼에 의해 생성되므로 별도의 생성자를 지원하지 않습니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
class PreAssignmentApplication : Application(){

    /**
     * 애플리케이션이 시작될때 설정 절차입니다.
     * 본 프로젝트에서는 DI 이외의 다른 설정은 넣지 않았습니다.
     */
    override fun onCreate() {
        super.onCreate()
        setDependencyInjection()
    }

    /**
     * Koin 을 활용한 Dependency Injection 입니다.
     * 전체 애플리케이션 프로세스에서 1회만 실행됩니다.
     * 상세한 DI 트리는 di.PreAssignmentApplicationModule 에 정의되어있습니다.
     *
     * @see com.gmail.ayteneve93.apex.kakaopay_preassignment.di.PreAssignmentApplicationModule
     */
    private fun setDependencyInjection() {
        startKoin {
            // DI - Android Context 를 Application 레벨에서 지정
            androidContext(this@PreAssignmentApplication)
            // DI - 앱 전체의 모듈들 정의
            modules(PreAssignmentApplicationModule)
        }
    }

}
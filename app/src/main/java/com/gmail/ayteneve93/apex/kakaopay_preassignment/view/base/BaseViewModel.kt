package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * 앱에서 사용하는 기본 뷰 모델, 공통으로 추가할 사항 있으면 아래에 내용 써 넣어야 함
 * 주의 : 반드시 Dependency Injection 패키지에서 관리해줄 것
 */
abstract class BaseViewModel(application : Application) : AndroidViewModel(application) {
}
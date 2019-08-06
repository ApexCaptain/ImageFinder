package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * MVVM 디자인 패턴을 구축할 때 사용한 기본 ViewModel 클래스입니다.
 * 이번 프로젝트에서는 예하 전체 ViewModel 에서 공통적으로 사용하는
 * 메소드나 속성이 없습니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
abstract class BaseViewModel(application : Application) : AndroidViewModel(application)
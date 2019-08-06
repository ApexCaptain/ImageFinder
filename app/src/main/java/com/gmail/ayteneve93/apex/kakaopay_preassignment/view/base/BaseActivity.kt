package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * MVVM 디자인 패턴을 구축할 때 사용한 기본 Activity 클래스입니다.
 *
 * @property mViewDataBinding 데이터바인딩 객체입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
abstract class BaseActivity<T: ViewDataBinding, V: BaseViewModel> : AppCompatActivity() {


    protected lateinit var mViewDataBinding: T

    /** 액티비티와 연결할 Layout 의 Id를 반환하는 추상 메소드입니다. */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /*** 뷰 모델을 가져오는 추상 메소드입니다.*/
    abstract fun getViewModel(): V

    /** BR 에서 viewModel 로 옵션을 잡아 반환하는 추상 메소드입니다. */
    abstract fun getBindingVariable(): Int

    /**
     * onCreate 작업 이후에 실행되는 추상 메소드입니다.
     * 액티비티가 모든 작동준비를 마친 후에 실행됩니다.
     */
    abstract fun setUp()

    /**
     * Activity 생명주기 onCreate 에 다음의 내용을 실행합니다.
     * 데이터 바인딩에 Binding 변수와 뷰 모델을 지정하고 생명주기 관리자를 본 액티비티 지정합니다.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.setVariable(getBindingVariable(), getViewModel())
        mViewDataBinding.executePendingBindings()
        setUp()
    }

}
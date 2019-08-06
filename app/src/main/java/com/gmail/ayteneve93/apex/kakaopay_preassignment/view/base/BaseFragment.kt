package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * MVVM 디자인 패턴을 구축할 때 사용한 기본 Fragment 클래스입니다.
 *
 * @property mViewDataBinding 데이터바인딩 객체입니다.
 * @property mActivity 상위 액티비티입니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
abstract class BaseFragment<T: ViewDataBinding, V: BaseViewModel> : Fragment() {

    protected lateinit var mViewDataBinding: T
    protected var mActivity: BaseActivity<*, *>? = null

    /** 프래그먼트와 연결할 Layout 의 Id를 반환하는 추상 메소드입니다. */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /*** 뷰 모델을 가져오는 추상 메소드입니다.*/
    abstract fun getViewModel(): V

    /** BR 에서 viewModel 로 옵션을 잡아 반환하는 추상 메소드입니다. */
    abstract fun getBindingVariable(): Int

    /**
     * onViewCreated 작업 이후에 실행되는 추상 메소드입니다.
     * 프래그먼트가 모든 작동준비를 마친 후에 실행됩니다.
     */
    abstract fun setUp()

    /**
     * Fragment 생명주기 onAttach 에 다음의 내용을 실행합니다.
     * Context 가 BaseActivity 일 경우 상위 액티비티에 Context 를 저장합니다
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is BaseActivity<*, *>) {
            mActivity = context
        }
    }

    /**
     * Fragment 생명주기 onCreateView 에 다음의 내용을 실행합니다.
     * DataBindingUtil 로부터 지정한 레이아웃 파일에 맞는 DataBinding 객체를 가져와서 저장합니다.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mViewDataBinding.root
    }

    /**
     * Fragment 생명주기 onViewCreated 에 다음의 내용을 실행합니다.
     * 데이터 바인딩에 Binding 변수와 뷰 모델을 지정하고 생명주기 관리자를 본 프래그먼트로 지정합니다.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding.setVariable(getBindingVariable(), getViewModel())
        mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.executePendingBindings()
        setUp()
    }

    /**
     * 본 Fragment 를 사용하는 상위 Activity 를 가져옵니다.
     *
     * @return 상위 액티비티입니다.
     */
    fun getBaseActivity() : BaseActivity<*, *>? {
        return mActivity
    }

    /** 상위 액티비티와의 연결을 제거합니다. */
    override fun onDetach() {
        mActivity = null
        super.onDetach()
    }

}
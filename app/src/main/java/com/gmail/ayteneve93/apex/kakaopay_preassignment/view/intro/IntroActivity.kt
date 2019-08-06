package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainActivity

/**
 * 앱 실행시 나타나는 Into 화면입니다.
 * 따로 ViewModel 이 필요한 액티비티가 아니므로
 * 예외적으로 MVVM 패턴에서 제외되었습니다.
 * 기본적으로 0.5초의 시간이 진행된 후에 MainActivity 로 화면을 전환합니다.
 *
 * @author ayteneve93@gmail.com
 *
 */
class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 500)
    }
}

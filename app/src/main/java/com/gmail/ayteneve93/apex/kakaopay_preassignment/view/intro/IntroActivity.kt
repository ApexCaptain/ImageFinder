package com.gmail.ayteneve93.apex.kakaopay_preassignment.view.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.gmail.ayteneve93.apex.kakaopay_preassignment.R
import com.gmail.ayteneve93.apex.kakaopay_preassignment.view.main.MainActivity

// 이번 앱의 인트로는 추가적인 프로세스가 요구되지 않으므로 뷰 모델을 작성하지 않음
class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 500)
    }
}

package com.hola360.crushlovecalculator

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.lottie.LottieAnimationView

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val animationView= findViewById<LottieAnimationView>(R.id.animationView)
        animationView.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        })
    }

}
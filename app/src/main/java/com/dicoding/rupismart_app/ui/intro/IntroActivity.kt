package com.dicoding.rupismart_app.ui.intro

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.rupismart_app.MainActivity
import com.dicoding.rupismart_app.databinding.ActivityIntroBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        splashScreenHandler()
        playAnimation()
    }

    private fun playAnimation() {
        val rotation = ObjectAnimator.ofFloat(binding.ivIntro, View.ROTATION, 0f, 360f)
        val fade = ObjectAnimator.ofFloat(binding.ivIntro, View.ALPHA,  1f)

        AnimatorSet().apply {
            playTogether(rotation, fade)
            startDelay = 500
            duration = 1000
        }.start()
    }


    private fun splashScreenHandler() {
        lifecycleScope.launch {
            delay(1500)
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
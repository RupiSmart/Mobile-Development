package com.dicoding.rupismart_app.ui.setting

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.databinding.ActivitySettingBinding
import com.dicoding.rupismart_app.utils.themeIsDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private val viewModel by viewModels<SettingViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        playAnimation()
        setupView()
        setupAction()

        binding.mainAppBar.setNavigationOnClickListener {
            lifecycleScope.launch {
                ObjectAnimator.ofFloat(it, View.TRANSLATION_X, 0f, -200f).apply {
                    duration = 500
                    start()
                }
                delay(100)
                onBackPressedDispatcher.onBackPressed()
                delay(500)
                it.translationX = 0f
            }
        }
        binding.changeLanguageIcon.setOnClickListener{
            lifecycleScope.launch {
                ObjectAnimator.ofFloat(it, View.TRANSLATION_X, 0f, 200f).apply {
                    duration = 500
                    start()
                }
                delay(100)
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                delay(500)
                it.translationX = 0f
            }
        }
        binding.talkbackActive.setOnClickListener{
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        viewModel.darkMode.observe(this) { isDarkMode ->
            themeIsDark(isDarkMode)
            binding.switchMode.isChecked = isDarkMode
            binding.tvModeSetting.text = if (!isDarkMode) "Light Mode" else "Dark Mode"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {

        val locale = Locale.getDefault()
        binding.tvLangSetting.text = "${locale.displayLanguage} ${ifSameLangCountry(locale)}"
    }
    private fun setupAction(){
        binding.switchMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.toggleDarkMode(isChecked)
        }
    }

    private fun playAnimation(){
        val appBar = ObjectAnimator.ofFloat(binding.appBarLayout, View.ALPHA, 1F).setDuration(400)
        val tvLangSetting = ObjectAnimator.ofFloat(binding.tvLangSetting, View.ALPHA, 1F).setDuration(400)
        val rlLangSetting = ObjectAnimator.ofFloat(binding.rlLangSetting, View.ALPHA, 1F).setDuration(400)
        val tvPrefSettingTitle = ObjectAnimator.ofFloat(binding.tvPrefSettingTitle, View.ALPHA, 1F).setDuration(400)
        val rlPrefSetting = ObjectAnimator.ofFloat(binding.rlPrefSetting, View.ALPHA, 1F).setDuration(400)
        AnimatorSet().apply {
            playTogether(
                appBar,
                tvLangSetting,
                rlLangSetting,
                tvPrefSettingTitle,
                rlPrefSetting
            )
            startDelay = 500
            start()
        }
    }

    private fun ifSameLangCountry(locale: Locale): String {
        if(locale.displayCountry!=locale.displayLanguage){
            return "(${locale.displayCountry})"
        }
        return ""
    }
}
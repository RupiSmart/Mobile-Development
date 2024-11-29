package com.dicoding.rupismart_app.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.rupismart_app.databinding.ActivitySettingBinding
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupView()
        binding.mainAppBar.setNavigationOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {

        val locale = Locale.getDefault()
        binding.tvLangSetting.text = "${locale.displayLanguage} ${ifSameLangCountry(locale)}"
    }

    private fun ifSameLangCountry(locale: Locale): String {
        if(locale.displayCountry!=locale.displayLanguage){
            return "(${locale.displayCountry})"
        }
        return ""
    }
}
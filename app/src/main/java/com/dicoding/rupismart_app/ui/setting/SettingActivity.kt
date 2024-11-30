package com.dicoding.rupismart_app.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.rupismart_app.ViewModelFactory
import com.dicoding.rupismart_app.databinding.ActivitySettingBinding
import com.dicoding.rupismart_app.utils.ThemeisDark
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
        setupView()
        setupAction()

        binding.mainAppBar.setNavigationOnClickListener { onBackPressed() }

        viewModel.darkMode.observe(this) { isDarkMode ->
            ThemeisDark(isDarkMode)
            binding.swithMode.isChecked = isDarkMode
            binding.tvModeSetting.text = if (!isDarkMode) "Light Mode" else "Dark Mode"
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {

        val locale = Locale.getDefault()
        binding.tvLangSetting.text = "${locale.displayLanguage} ${ifSameLangCountry(locale)}"
    }
    private fun setupAction(){
        binding.swithMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.toggleDarkMode(isChecked)
        }
    }

    private fun ifSameLangCountry(locale: Locale): String {
        if(locale.displayCountry!=locale.displayLanguage){
            return "(${locale.displayCountry})"
        }
        return ""
    }
}
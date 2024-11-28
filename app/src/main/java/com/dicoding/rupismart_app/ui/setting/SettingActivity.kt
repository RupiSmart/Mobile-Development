package com.dicoding.rupismart_app.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.rupismart_app.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.mainAppBar.setNavigationOnClickListener { onBackPressed() }
    }
}
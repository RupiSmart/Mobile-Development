package com.dicoding.rupismart_app.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository

class IntroViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {

    fun getDarkMode(): LiveData<Boolean> {
        return rupiSmartRepository.getDarkMode()
    }
}
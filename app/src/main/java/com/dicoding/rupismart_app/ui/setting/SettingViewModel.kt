package com.dicoding.rupismart_app.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.rupismart_app.RupiSmartRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {
    val darkMode: LiveData<Boolean> = rupiSmartRepository.getDarkMode()

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            rupiSmartRepository.saveDarkMode(isDarkMode)
        }
    }
}
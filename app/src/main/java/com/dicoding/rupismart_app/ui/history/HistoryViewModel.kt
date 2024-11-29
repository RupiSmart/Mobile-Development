package com.dicoding.rupismart_app.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository

class HistoryViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {
        val getAllHistory = rupiSmartRepository.getAllHistory()

}
package com.dicoding.rupismart_app.ui.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HelpViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {

    val getAllHelp = rupiSmartRepository.getAllHelp()
}
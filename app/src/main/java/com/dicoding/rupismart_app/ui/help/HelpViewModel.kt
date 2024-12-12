package com.dicoding.rupismart_app.ui.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository
import com.dicoding.rupismart_app.data.remote.response.HelpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.dicoding.rupismart_app.data.Result
class HelpViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {

    fun getAllHelp(locale: String): LiveData<Result<HelpResponse>> {
        val result = rupiSmartRepository.getAllHelp(locale)
        return result ?: rupiSmartRepository.getAllHelp("id")
    }
}
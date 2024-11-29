package com.dicoding.rupismart_app.ui.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository

class ScanViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Scan Fragment"
    }
    val text: LiveData<String> = _text
}
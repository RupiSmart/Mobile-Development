package com.dicoding.rupismart_app.ui.help

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository

class HelpViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is help Fragment"
    }
    val text: LiveData<String> = _text
}
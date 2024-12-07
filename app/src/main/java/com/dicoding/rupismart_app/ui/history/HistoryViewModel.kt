package com.dicoding.rupismart_app.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.rupismart_app.RupiSmartRepository
import com.dicoding.rupismart_app.data.local.entity.HistoryEntity
import com.dicoding.rupismart_app.data.remote.response.HistoryResponse
import com.dicoding.rupismart_app.data.Result

class HistoryViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {
        val getAllHistory: LiveData<Result<List<HistoryEntity>>> = rupiSmartRepository.getAllHistory()

}
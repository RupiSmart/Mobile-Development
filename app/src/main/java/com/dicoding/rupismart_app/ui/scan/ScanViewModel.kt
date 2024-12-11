package com.dicoding.rupismart_app.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.rupismart_app.RupiSmartRepository
import kotlinx.coroutines.launch
import java.io.File
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.data.remote.response.PredictResponse

class ScanViewModel(private val rupiSmartRepository: RupiSmartRepository) : ViewModel() {
    private val _uploadResult = MutableLiveData<Result<PredictResponse>>()
    val uploadResult: LiveData<Result<PredictResponse>> get() = _uploadResult
    val uploadStatus = MutableLiveData<Boolean>()

    fun uploadImage(context: Context,imageFile: File) = rupiSmartRepository.uploadImage(context,imageFile)

    fun saveToHistory(label: String, index: Int) = rupiSmartRepository.saveToHistory(label,index)
}
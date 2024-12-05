package com.dicoding.rupismart_app.ui.scan

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
fun upload(imageFile: File) = rupiSmartRepository.uploadImage(imageFile)
    fun uploadImage(imageFile: File) {
        rupiSmartRepository.uploadImage(imageFile).observeForever { result ->
            _uploadResult.postValue(result)

            when (result) {
                is Result.Success -> {
                    uploadStatus.postValue(true)
                }
                is Result.Error -> {
                    uploadStatus.postValue(false)
                    Log.e("ScanViewModel", "Upload error: ${result.error}")
                }
                is Result.Loading -> {
                    uploadStatus.postValue(false)
                }
            }
        }
    }
}
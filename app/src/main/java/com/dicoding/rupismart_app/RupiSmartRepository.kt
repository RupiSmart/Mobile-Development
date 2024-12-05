package com.dicoding.rupismart_app

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.dicoding.rupismart_app.data.pref.RupiSmartPreference
import com.dicoding.rupismart_app.data.remote.response.HelpResponse
import com.dicoding.rupismart_app.data.remote.retrofit.ApiService
import com.dicoding.rupismart_app.utils.AppExecutors
import com.google.gson.Gson
import retrofit2.HttpException
import com.dicoding.rupismart_app.data.remote.response.HistoryResponse
import com.dicoding.rupismart_app.data.remote.response.PredictResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.data.remote.retrofit.RetrofitInstance
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RupiSmartRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val rupismartPreference: RupiSmartPreference
) {
    fun getDarkMode(): LiveData<Boolean> {
        return rupismartPreference.getDarkMode().asLiveData()
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        rupismartPreference.saveDarkMode(isDarkMode)
    }
    @SuppressLint("SuspiciousIndentation")
    fun getAllHistory(): LiveData<Result<HistoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllHistory()
                emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, HistoryResponse::class.java)
            emit(Result.Error(errorBody.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
    fun getAllHelp(): LiveData<Result<HelpResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllHelp()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, HelpResponse::class.java)
            emit(Result.Error(errorBody.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }


    }
     fun uploadImage(imageFile: File): LiveData<Result<PredictResponse>> = liveData {
        emit(Result.Loading)
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://rupismart-ml-138626623402.asia-southeast1.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val services = retrofit.create(ApiService::class.java)
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = services.uploadImage(body)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, HelpResponse::class.java)
            emit(Result.Error(errorBody.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

    }

    companion object {
        @Volatile
        private var instance: RupiSmartRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors,
            rupismartPreferences: RupiSmartPreference
        ): RupiSmartRepository =
            instance ?: synchronized(this) {
                instance ?: RupiSmartRepository(apiService, appExecutors, rupismartPreferences)
            }.also { instance = it }
    }

}

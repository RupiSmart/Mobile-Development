package com.dicoding.rupismart_app

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.rupismart_app.data.pref.RupiSmartPreference
import com.dicoding.rupismart_app.data.remote.response.HelpResponse
import com.dicoding.rupismart_app.data.remote.retrofit.ApiService
import com.dicoding.rupismart_app.utils.AppExecutors
import com.google.gson.Gson
import retrofit2.HttpException
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.data.remote.response.HistoryResponse

class RupiSmartRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val rupismartPreference: RupiSmartPreference
) {
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

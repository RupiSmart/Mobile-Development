package com.dicoding.rupismart_app

import com.dicoding.rupismart_app.data.pref.RupiSmartPreference
import com.dicoding.rupismart_app.data.remote.retrofit.ApiService
import com.dicoding.rupismart_app.utils.AppExecutors

class RupiSmartRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val rupismartPreference: RupiSmartPreference
) {

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

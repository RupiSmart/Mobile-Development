package com.dicoding.rupismart_app.di

import android.content.Context
import com.dicoding.rupismart_app.RupiSmartRepository
import com.dicoding.rupismart_app.data.pref.RupiSmartPreference
import com.dicoding.rupismart_app.data.pref.dataStore
import com.dicoding.rupismart_app.data.remote.retrofit.ApiConfig
import com.dicoding.rupismart_app.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): RupiSmartRepository {
        val apiService = ApiConfig.getApiService()
        val appExecutors = AppExecutors()
        val pref = RupiSmartPreference.getInstance(context.dataStore)
        return RupiSmartRepository.getInstance(apiService, appExecutors,pref)
    }

}
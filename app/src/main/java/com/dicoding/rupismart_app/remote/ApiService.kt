package com.dicoding.rupismart_app.remote

import com.dicoding.rupismart_app.response.HelpResponse
import retrofit2.http.GET

interface ApiService {
    @GET("help")
    suspend fun getAllHelp(
    ) : HelpResponse

    @GET("help")
    fun getAllHistory(
    ) : HelpResponse
}
package com.dicoding.rupismart_app.data.remote.retrofit

    import com.dicoding.rupismart_app.data.remote.response.HistoryResponse
    import retrofit2.Call
    import retrofit2.http.*

    interface ApiService {

        @GET("history")
        fun getAllHistory() : HistoryResponse
    }
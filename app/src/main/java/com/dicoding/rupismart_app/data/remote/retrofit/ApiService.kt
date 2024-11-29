package com.dicoding.rupismart_app.data.remote.retrofit

    import com.dicoding.rupismart_app.data.remote.response.HelpResponse
    import com.dicoding.rupismart_app.data.remote.response.HistoryResponse
    import retrofit2.http.GET

interface ApiService {
    @GET("help")
    suspend fun getAllHelp(
    ) : HelpResponse

    @GET("history")
    suspend fun getAllHistory(
    ) : HistoryResponse
}
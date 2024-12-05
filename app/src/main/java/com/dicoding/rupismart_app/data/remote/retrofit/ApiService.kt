package com.dicoding.rupismart_app.data.remote.retrofit

    import com.dicoding.rupismart_app.data.remote.response.HelpResponse
    import com.dicoding.rupismart_app.data.remote.response.HistoryResponse
    import com.dicoding.rupismart_app.data.remote.response.PredictResponse
    import okhttp3.MultipartBody
    import retrofit2.Response
    import retrofit2.http.GET
    import retrofit2.http.Multipart
    import retrofit2.http.POST
    import retrofit2.http.Part

interface ApiService {
    @GET("help")
    suspend fun getAllHelp(
    ) : HelpResponse

    @GET("history")
    suspend fun getAllHistory(
    ) : HistoryResponse

    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): PredictResponse
}
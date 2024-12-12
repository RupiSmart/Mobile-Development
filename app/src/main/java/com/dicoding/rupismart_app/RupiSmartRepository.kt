package com.dicoding.rupismart_app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.dicoding.rupismart_app.data.pref.RupiSmartPreference
import com.dicoding.rupismart_app.data.remote.response.HelpResponse
import com.dicoding.rupismart_app.data.remote.retrofit.ApiService
import com.dicoding.rupismart_app.utils.AppExecutors
import com.google.gson.Gson
import retrofit2.HttpException
import com.dicoding.rupismart_app.data.remote.response.PredictResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.dicoding.rupismart_app.data.local.entity.HistoryEntity
import com.dicoding.rupismart_app.data.local.room.RupismartDao
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import com.dicoding.rupismart_app.data.Result
import com.dicoding.rupismart_app.utils.getNominal

class RupiSmartRepository private constructor(
    private val apiService: ApiService,
    private val dao: RupismartDao,
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
    fun getAllHistory(): LiveData<Result<List<HistoryEntity>>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<List<HistoryEntity>>> = dao.getAllHistory().map { Result.Success(it) }
        emitSource(localData)
    }

    fun getAllHelp(locale:String): LiveData<Result<HelpResponse>> = liveData {
        emit(Result.Loading)
        try {
        val lang=locale.lowercase()
            val response =  apiService.getAllHelp(lang)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, HelpResponse::class.java)
            emit(Result.Error(errorBody.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }


    }
     fun uploadImage(context:Context,imageFile: File): LiveData<Result<PredictResponse>> = liveData {
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
    private fun getImageUriFromFile(context: Context, imageFile: File): Uri {
        return Uri.fromFile(imageFile)
    }

    private fun compressAndConvertImageToBase64(context: Context, imageUri: Uri): String {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true)
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveToHistory(label: String,index:Int) {
        val milis=System.currentTimeMillis()
                val analysisEntity = HistoryEntity(
                    id = 0,
                    nominal = getNominal(index),
                    label = label,
                    timestamp = milis.toString()
                )
                dao.insertHistory(analysisEntity)

            if(dao.getHistoryCount()>100)
            {
                dao.deleteLatestHistory()
            }
    }


    companion object {
        @Volatile
        private var instance: RupiSmartRepository? = null
        fun getInstance(
            apiService: ApiService,
            dao: RupismartDao,
            appExecutors: AppExecutors,
            rupismartPreferences: RupiSmartPreference
        ): RupiSmartRepository =
            instance ?: synchronized(this) {
                instance ?: RupiSmartRepository(apiService, dao,appExecutors, rupismartPreferences)
            }.also { instance = it }
    }

}

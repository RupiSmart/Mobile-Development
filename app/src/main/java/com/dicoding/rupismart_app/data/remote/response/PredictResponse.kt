package com.dicoding.rupismart_app.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("success")
	val success: Boolean
)
data class PredictionTime(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("timezone")
	val timezone: String,

	@field:SerializedName("time")
	val time: String
)

data class Result(

	@field:SerializedName("image_url")
	val imageUrl: String,

	@field:SerializedName("authenticity")
	val authenticity: String,

	@field:SerializedName("confidence")
	val confidence: Any,

	@field:SerializedName("prediction_time")
	val predictionTime: PredictionTime
)

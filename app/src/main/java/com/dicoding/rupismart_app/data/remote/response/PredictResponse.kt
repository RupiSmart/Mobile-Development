package com.dicoding.rupismart_app.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("success")
	val success: Boolean
)

data class Result(

	@field:SerializedName("nominal")
	val nominal: String,

	@field:SerializedName("isKoin")
	val isKoin: Boolean,

	@field:SerializedName("auth_confidence")
	val authConfidence: Any,

	@field:SerializedName("authenticity")
	val authenticity: String,

	@field:SerializedName("nominal_confidence")
	val nominalConfidence: Any
)

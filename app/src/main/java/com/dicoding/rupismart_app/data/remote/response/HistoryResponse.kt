package com.dicoding.rupismart_app.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("history")
	val history: List<HistoryItem>
)

data class HistoryItem(

	@field:SerializedName("isReal")
	val isReal: Boolean,

	@field:SerializedName("nominal")
	val nominal: Int,

	@field:SerializedName("isKoin")
	val isKoin: Boolean,

	@field:SerializedName("confidence")
	val confidence: Any,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("position")
	val position: Any,

	@field:SerializedName("timestamp")
	val timestamp: String
)

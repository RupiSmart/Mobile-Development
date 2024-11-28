package com.dicoding.rupismart_app.response

import com.google.gson.annotations.SerializedName

data class HelpResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem>
)

data class CategoriesItem(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("title")
	val title: String
)

package kr.co.bigwalk.app.data.organization.space

import com.google.gson.annotations.SerializedName

data class SpaceOptionResponse(
    @SerializedName("content")
    val content: String,
    @SerializedName("placeholder")
    val placeholder: String,
    @SerializedName("necessary")
    val isNecessary: Boolean
)
package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName

data class LabelCategoryListResponse(
    val data: List<LabelCategoryResponse>
)

data class LabelCategoryResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("seq")
    val seq: Int,
    @SerializedName("name")
    val name: String
)

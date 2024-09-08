package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName

data class ModifyCrewInfoResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("mainImagePath")
    val mainImagePath: String
)
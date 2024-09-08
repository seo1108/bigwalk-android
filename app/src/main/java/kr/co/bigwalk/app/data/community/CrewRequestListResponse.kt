package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName

data class CrewRequestListResponse(
    val content: List<CrewRequestResponse>
)

data class CrewRequestResponse(

    @SerializedName("id")
    val id: Long = -1L,
    @SerializedName("userName")
    val userName: String = "",
    @SerializedName("profilePath")
    val profilePath: String = "",
    @SerializedName("crewCampaignName")
    val crewCampaignName: String = "",
    @SerializedName("question")
    val question: String = "",
    @SerializedName("answer")
    val answer: String = ""
)
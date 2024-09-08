package kr.co.bigwalk.app.data.community.dto

import com.google.gson.annotations.SerializedName

enum class LabelStatus {
    SCREENING
}

enum class LabelType {
    MISSION
}

data class MySupportersCampaignsResponse(
    @SerializedName("crewCampaigns")
    val list: List<SupportersCampaignsResponse>
)

data class SupportersCampaignsResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("thumbnailPath")
    val thumbnailPath: String?,
    @SerializedName("sequence")
    val sequence: Int,
    @SerializedName("hasActionMission")
    val hasActionMission: Boolean
)

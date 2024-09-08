package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.funding.CampaignRegisterStatus

data class CrewCampaignResponse(
    @SerializedName("mainImage")
    val mainImage: String?,
    @SerializedName("crewCampaignRegisterStatus")
    val crewCampaignRegisterStatus: CampaignRegisterStatus?,
    @SerializedName("categoryName")
    val categoryName: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("subTitle")
    val subTitle: String?,
    @SerializedName("summary")
    val summary: String?,
    @SerializedName("hasActionMission")
    val hasActionMission: Boolean?,
    @SerializedName("contentPreviews")
    val contentPreviews: List<ContentPreviewResponse>?
)

data class ContentPreviewResponse (
    @SerializedName("title")
    val title: String?,
    @SerializedName("contentImagePath")
    val contentImagePath: String?,
    @SerializedName("description")
    val description: String?
)

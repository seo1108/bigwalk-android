package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.funding.CampaignRegisterStatus

data class ChallengeOfCrewCampaignResponse(
    @SerializedName("title")
    val title: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("crewCampaignRegisterStatus")
    val crewCampaignRegisterStatus: CampaignRegisterStatus?,
    @SerializedName("mainImagePath")
    val mainImagePath: String?,
    @SerializedName("categoryName")
    val categoryName: String?,
    @SerializedName("firstHowToImagePath")
    val firstHowToImagePath: String?,
    @SerializedName("secondHowToImagePath")
    val secondHowToImagePath: String?,
    @SerializedName("firstHowToDescription")
    val firstHowToDescription: String?,
    @SerializedName("secondHowToDescription")
    val secondHowToDescription: String?,
    @SerializedName("firstInvalidImagePath")
    val firstInvalidImagePath: String?,
    @SerializedName("secondInvalidImagePath")
    val secondInvalidImagePath: String?,
    @SerializedName("firstInvalidDescription")
    val firstInvalidDescription: String?,
    @SerializedName("secondInvalidDescription")
    val secondInvalidDescription: String?
)

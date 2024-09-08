package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.funding.CampaignContentType
import kr.co.bigwalk.app.data.funding.CampaignRegisterStatus
import kr.co.bigwalk.app.data.funding.LabelSignUpMethod

data class LabelBeforeDataResponse(
    @SerializedName("title")
    val title: String,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("maxDonationPerDay")
    val maxDonationPerDay: Long? = null,
    @SerializedName("subTitle")
    val subTitle: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("crewCampaignSignUpMethod")
    val crewCampaignSignUpMethod: LabelSignUpMethod,
    @SerializedName("question")
    val question: String? = null,
    @SerializedName("campaignImagePath")
    val campaignImagePath: String,
    @SerializedName("status")
    val status: CampaignRegisterStatus,
    @SerializedName("campaignContentResponses")
    val campaignContentResponses: List<CampaignContentResponse>,
    @SerializedName("actionMission")
    val actionMission: ActionMissionResponse? = null
)

data class CampaignContentResponse(
    @SerializedName("campaignContentType")
    val campaignContentType: CampaignContentType,
    @SerializedName("contentTitle")
    val contentTitle: String,
    @SerializedName("contentDescription")
    val contentDescription: String,
    @SerializedName("contentImagePath")
    val contentImagePath: String,
    @SerializedName("sequence")
    val sequence: Int
)

data class ActionMissionResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("mainImagePath")
    val mainImagePath: String,
    @SerializedName("firstHowToImagePath")
    val firstHowToImagePath: String,
    @SerializedName("secondHowToImagePath")
    val secondHowToImagePath: String,
    @SerializedName("firstInvalidImagePath")
    val firstInvalidImagePath: String,
    @SerializedName("secondInvalidImagePath")
    val secondInvalidImagePath: String,
    @SerializedName("firstHowToDescription")
    val firstHowToDescription: String,
    @SerializedName("secondHowToDescription")
    val secondHowToDescription: String,
    @SerializedName("firstInvalidDescription")
    val firstInvalidDescription: String,
    @SerializedName("secondInvalidDescription")
    val secondInvalidDescription: String
)

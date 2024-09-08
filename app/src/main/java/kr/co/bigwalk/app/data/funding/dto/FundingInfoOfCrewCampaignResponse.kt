package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName

data class FundingInfoOfCrewCampaignResponse(
    @SerializedName("fundingInfoImagePath")
    val fundingInfoImagePath: String?,
    @SerializedName("earlyFundingInfoImagePath")
    val earlyFundingInfoImagePath: String?,
    @SerializedName("earlyBirdRemainCount")
    val earlyBirdRemainCount: Int?,
    @SerializedName("todayRemainingSteps")
    val todayRemainingSteps: Int?,
    @SerializedName("groupRole")
    val groupRole: String?
)
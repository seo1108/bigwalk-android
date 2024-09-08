package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName

data class MyFundingDescriptionResponse(
    @SerializedName("promoterName")
    val promoterName: String,
    @SerializedName("crewCampaignCount")
    val crewCampaignCount: Long
)

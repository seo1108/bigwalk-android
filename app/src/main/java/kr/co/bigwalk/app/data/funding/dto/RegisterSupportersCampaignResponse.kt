package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName

data class RegisterSupportersCampaignResponse(
    @SerializedName("crewCampaignId")
    val labelId: Long
)
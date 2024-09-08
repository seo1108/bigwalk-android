package kr.co.bigwalk.app.data.campaign.dto

import com.google.gson.annotations.SerializedName

data class DonateResponse(
    @SerializedName("isMax")
    val isMax: Boolean,
    @SerializedName("campaignId")
    val campaignId: Int,
    @SerializedName("todayDonatedSteps")
    val todayDonatedSteps: Long,
    @SerializedName("maxDonationPerDay")
    val maxDonationPerDay: Long?
)

data class HotTimeDonateResponse(
    @SerializedName("isMax")
    val isMax: Boolean,
    @SerializedName("campaignId")
    val campaignId: Int,
    @SerializedName("todayDonatedSteps")
    val todayDonatedSteps: Long,
    @SerializedName("maxDonationPerDay")
    val maxDonationPerDay: Long?
)
package kr.co.bigwalk.app.data.report.dto

import com.google.gson.annotations.SerializedName

data class DonationRatioResponse(
    val environmentDonationRatio: Double,
    val animalDonationRatio: Double,
    val childDonationRatio: Double,
    val disabledDonationRatio: Double,
    @SerializedName("senileDonationRatio")
    val oldManDonationRatio: Double,
    val globalDonationRatio: Double
)

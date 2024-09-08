package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName

data class RefundForFundingStepsResponse(
    @SerializedName("refundedSteps")
    val refundedSteps: Long,
    @SerializedName("donableSteps")
    val donableSteps: Int
)
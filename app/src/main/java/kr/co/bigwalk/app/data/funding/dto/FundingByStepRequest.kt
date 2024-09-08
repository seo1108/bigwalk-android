package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName

data class FundingByStepRequest(
    @SerializedName("fundingSteps")
    val fundingSteps: Int
)

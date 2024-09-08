package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.extension.valueToCommaString

data class RewardFundingInfoResponse(
    @SerializedName("fundingSteps")
    val fundingSteps: Long,
    @SerializedName("rewardSteps")
    val rewardSteps: Long,
    @SerializedName("isEarlyBird")
    val isEarlyBird: Boolean
) {
    fun getFundingStepsToString() = fundingSteps.valueToCommaString()
    fun getRewardStepsToString() = rewardSteps.valueToCommaString()
}
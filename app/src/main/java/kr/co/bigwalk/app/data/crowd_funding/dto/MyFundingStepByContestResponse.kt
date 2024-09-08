package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.extension.valueToCommaString

data class MyFundingStepByContestResponse(
    @SerializedName("myMaxFundingSteps")
    val myMaxFundingSteps: Long?,
    @SerializedName("todayRemainingSteps")
    val todayRemainingSteps: Long?
) {
    fun myMaxFundingStepsToString(): String = myMaxFundingSteps?.valueToCommaString() ?: "0"
    fun todayRemainingStepsToString(): String = todayRemainingSteps?.valueToCommaString() ?: "0"
}

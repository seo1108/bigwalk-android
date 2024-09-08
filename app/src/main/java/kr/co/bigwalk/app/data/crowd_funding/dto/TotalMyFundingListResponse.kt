package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.extension.valueToCommaString

data class TotalMyFundingListResponse(
    @SerializedName("content")
    val content: List<TotalMyFundingResponse>
)

data class TotalMyFundingResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("crewCampaignTitle")
    val crewCampaignTitle: String,
    @SerializedName("mainImage")
    val mainImage: String,
    @SerializedName("crewTitle")
    val crewTitle: String,
    @SerializedName("fundingState")
    val fundingState: FundingState,
    @SerializedName("totalFundingSteps")
    val totalFundingSteps: Long,
    @SerializedName("remainSteps")
    val remainSteps: Long,
    @SerializedName("myFundingSteps")
    val myFundingSteps: Long,
    @SerializedName("targetFundingSteps")
    val targetFundingSteps: Long,
    @SerializedName("reward")
    val reward: Long,
    @SerializedName("earlyAccessorReward")
    val earlyAccessorReward: Long,
    @SerializedName("isEarlyAccessor")
    val isEarlyAccessor: Boolean
) {
    fun getMyFundingStepsToString() = myFundingSteps.valueToCommaString()
    fun getTotalFundingStepsToString() = totalFundingSteps.valueToCommaString()
    fun getRemainToString(): String {
        return when (fundingState) {
            FundingState.FUNDING_SUCCESS -> {
                if (remainSteps <= 0) return "적용 완료"
                if (isEarlyAccessor) {
                    (myFundingSteps + reward + earlyAccessorReward).valueToCommaString()
                } else {
                    (myFundingSteps + reward).valueToCommaString()
                }
            }
            FundingState.FUNDING -> {
                myFundingSteps.valueToCommaString()
            }
            else -> {
                if (remainSteps <= 0) return "회수 완료"
                remainSteps.valueToCommaString()
            }
        }
    }
    fun getProgressPercent(): String {
        return "${((myFundingSteps.toFloat() / targetFundingSteps.toFloat()) * 100).toInt()}"
    }
}

enum class FundingState {
    @SerializedName("FUNDING")
    FUNDING,
    @SerializedName("FUNDING_SUCCESS")
    FUNDING_SUCCESS,
    @SerializedName("FUNDING_FAIL")
    FUNDING_FAIL,
    @SerializedName("FUNDING_FAILURE_TO_ACHIEVE")
    FUNDING_FAILURE_TO_ACHIEVE,
    @SerializedName("DELETE_FUNDING")
    DELETE_FUNDING,
    FUNDING_SUCCESS_AND_DONATE
}

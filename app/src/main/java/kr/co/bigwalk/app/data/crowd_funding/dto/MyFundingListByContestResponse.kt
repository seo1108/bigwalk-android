package kr.co.bigwalk.app.data.crowd_funding.dto

import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.extension.valueToCommaString

data class MyFundingListByContestResponse(
    val content: List<MyFundingByContestResponse>
)

data class MyFundingByContestResponse(
    val id: Long,
    val title: String,
    val crewTitle: String,
    val fundedSteps: Long,
    val targetFundingSteps: Long,
    val propensity: PropensityType
) {
    fun getFundedStepsToString() = fundedSteps.valueToCommaString()
    fun getProgressPercent(): Int {
        return ((fundedSteps.toFloat() / targetFundingSteps.toFloat()) * 100).toInt()
    }

    fun getProgressBarPercent(): Int {
        val minPercent = (fundedSteps.toFloat() / targetFundingSteps.toFloat() * 100).toInt()
        return if (minPercent > DEF_PERCENT) {
            minPercent
        } else {
            DEF_PERCENT
        }
    }

    companion object {
        private const val DEF_PERCENT = 5
    }
}

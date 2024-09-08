package kr.co.bigwalk.app.data.funding.dto

import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.extension.numberToUnit
import kr.co.bigwalk.app.extension.valueToCommaString

data class CrewCampaignDetailResponse(
    val mainImage: String,
    val crewCampaignRegisterStatus: FundingState,
    val categoryName: String,
    val title: String,
    val subTitle: String,
    val summary: String,
    val crewTitle: String,
    val contentPreviews: List<ContentPreviewResponse>,
    val fundedSteps: Long,
    val myFundingSteps: Long,
    val targetFundingSteps: Long,
    val isLike: Boolean,
    val isMemberBenefit: Boolean,
    val isInGroup: Boolean,
    val likeCount: Long,
    private val commentCount: Long,
    private val fundedCount: Long
) {
    fun getLikeCountToString() = likeCount.numberToUnit()
    fun getCommentsCountToString() = commentCount.numberToUnit()
    fun getFundedCountToString() = fundedCount.numberToUnit()
    fun getMyFundingStepsToString() = myFundingSteps.valueToCommaString()
    fun getFundedStepsToString() = fundedSteps.valueToCommaString()
}
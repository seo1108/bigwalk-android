package kr.co.bigwalk.app.data.crowd_funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.extension.numberToUnit
import kr.co.bigwalk.app.extension.valueToCommaString

data class FundingListByNewestResponse (
    val content: List<FundingByNewestResponse>
)

data class FundingByNewestResponse (
    val id: Long,
    val fundedSteps: Long,
    val title: String,
    val crewTitle: String,
    val mainImagePath: String,
    val groupPropensity: PropensityType
){
    fun toFundingByHottestResponse() =
        FundingByHottestResponse(
            id = id,
            title = title,
            crewTitle = crewTitle,
            mainImagePath = mainImagePath,
            groupPropensity = groupPropensity,
            fundedSteps = fundedSteps,
            likeCount = null,
            commentsCount = null,
            fundedCount = null,
            isLiked = null
        )
}

data class FundingListByHottestResponse (
    val data: List<FundingByHottestResponse>
)

data class FundingByHottestResponse (
    val id: Long,
    val title: String,
    val crewTitle: String,
    val mainImagePath: String,
    // 성향은 최신순에만 있음
    val groupPropensity: PropensityType,
    val fundedSteps: Long,
    //아래는 top10만 데이터 존재
    private val likeCount: Long?,
    private val commentsCount: Long?,
    @SerializedName("memberCount")
    private val fundedCount: Long?,
    val isLiked: Boolean?
) {
    fun getFundedStepsToString() = fundedSteps.valueToCommaString()
    fun getLikeCountToString() = likeCount?.numberToUnit()
    fun getCommentsCountToString() = commentsCount?.numberToUnit()
    fun getFundedCountToString() = fundedCount?.numberToUnit()
}
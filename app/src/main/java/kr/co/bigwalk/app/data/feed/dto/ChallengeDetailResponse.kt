package kr.co.bigwalk.app.data.feed.dto

data class ChallengeDetailResponse(
    val id: Long?,
    val mainImagePath: String?,
    val title: String?,
    val content: String?,
    var startDate: String? = "",
    var endDate: String? = "",
    val avgParticipations: String?,
    val myParticipations: String?,
    val myLikes: String?,
    val campaignId: Long?,
    val organizationId: Long?
)

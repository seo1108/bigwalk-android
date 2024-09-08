package kr.co.bigwalk.app.data.feed.dto

import kr.co.bigwalk.app.data.campaign.dto.ActionMissionResponse
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import java.time.LocalDateTime
import java.util.*

data class Feed(
    val actionDonationHistoryId: Long,
    val certifiedImagePath: String,
    val certifiedImagePath2: String?,
    val certifiedImagePath3: String?,
    val user: UserProfileResponse,
    val action: ActionMissionResponse,
    var likeCount: Long = 0,
    var commentCount: Long = 0,
    val campaignId: Long,
    var comment: String? = "",
    var liked: Boolean = false,
    var mine: Boolean = false,
    var isMyCampaign: Boolean = false,
    var currTime: String?,
    var likeText: String?
) {
    var isExpanded: Boolean = false
}
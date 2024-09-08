package kr.co.bigwalk.app.data.feedHome.dto

import java.io.Serializable

data class ChallengeInfoResponse(
    override val id: Long,
    override val mainImagePath: String,
    override val startDate: String?,
    override val endDate: String?,
    override var open: Boolean? = false,
    override val challengeType: String?,
    override val title: String?,
    override val remainDays: Long?
) : ChallengeInfoItem, Serializable

/*data class ChallengeInfoResponse(
    val id: Long?,
    val mainImagePath: String?,
    val startDate: String?,
    val endDate: String?,
    val open: Boolean?,
    val title: String?,
    val remainDays: Long?
)*/

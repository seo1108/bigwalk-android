package kr.co.bigwalk.app.data.feedHome.dto

interface ChallengeInfoItem {
    val id: Long?
    val mainImagePath: String?
    val startDate: String?
    val endDate: String?
    var open: Boolean?
    val challengeType: String?
    val title: String?
    val remainDays: Long?
}
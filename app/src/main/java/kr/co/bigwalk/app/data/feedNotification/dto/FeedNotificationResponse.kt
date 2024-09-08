package kr.co.bigwalk.app.data.feedNotification.dto

data class FeedNotificationResponse(
    val actionDonationHistoryId: Long,
    val createdBy: String,
    val value: String?,
    val imagePath: String,
    val notiType: Int,
    val createdTime: String
)
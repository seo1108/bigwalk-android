package kr.co.bigwalk.app.data.feedNotification.repository

import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse

interface FeedNotificationDataSource {

    interface GetFeedNotificationCallback {
        fun onSuccess(feedNotificationResponse: List<FeedNotificationResponse>)
        fun onFailed(reason: String)
    }
    fun getFeedNotifications(
        page: Int,
        size: Int,
        campaignId: Long,
        getFeedNotificationCallback: GetFeedNotificationCallback
    )

    fun getAllNotifications(
        page: Int,
        size: Int,
        getFeedNotificationCallback: GetFeedNotificationCallback
    )
}
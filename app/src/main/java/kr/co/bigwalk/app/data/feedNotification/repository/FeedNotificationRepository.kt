package kr.co.bigwalk.app.data.feedNotification.repository

object FeedNotificationRepository: FeedNotificationDataSource {

    private val feedNotificationRemoteDataSource = FeedNotificationRemoteDataSource

    override fun getFeedNotifications(
        page: Int,
        size: Int,
        campaignId: Long,
        getFeedNotificationCallback: FeedNotificationDataSource.GetFeedNotificationCallback
    ) {
        feedNotificationRemoteDataSource.getFeedNotifications(page, size, campaignId, getFeedNotificationCallback)
    }

    override fun getAllNotifications(
        page: Int,
        size: Int,
        getFeedNotificationCallback: FeedNotificationDataSource.GetFeedNotificationCallback
    ) {
        feedNotificationRemoteDataSource.getAllNotifications(page, size, getFeedNotificationCallback)
    }

}
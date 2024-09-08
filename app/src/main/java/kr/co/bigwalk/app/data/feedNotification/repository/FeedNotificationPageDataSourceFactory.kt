package kr.co.bigwalk.app.data.feedNotification.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse

class FeedNotificationPageDataSourceFactory(
    private val campaignId: Long
): DataSource.Factory<Int, FeedNotificationResponse>() {

    private val feedNotificationsLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, FeedNotificationResponse>>()

    override fun create(): DataSource<Int, FeedNotificationResponse> {
        val feedNotificationPageDataSource = FeedNotificationPageDataSource(campaignId)
        feedNotificationsLiveDataSource.postValue(feedNotificationPageDataSource)
        return feedNotificationPageDataSource
    }
}

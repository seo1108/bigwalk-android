package kr.co.bigwalk.app.data.feedNotification.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import kr.co.bigwalk.app.exception.MissionCampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedNotificationPageDataSource(
    private val campaignId: Long
): PageKeyedDataSource<Int, FeedNotificationResponse>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeedNotificationResponse>
    ) {
        FeedNotificationRepository.getAllNotifications(FIRST_PAGE, PAGE_SIZE, object : FeedNotificationDataSource.GetFeedNotificationCallback {
            override fun onSuccess(feedNotificationResponse: List<FeedNotificationResponse>) {
                callback.onResult(feedNotificationResponse, null, FIRST_PAGE + 1)
            }
            override fun onFailed(reason: String) {
                showToast("알림 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(MissionCampaignException(reason))
            }
        })
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, FeedNotificationResponse>
    ) {
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, FeedNotificationResponse>
    ) {
        FeedNotificationRepository.getAllNotifications(params.key, params.requestedLoadSize, object : FeedNotificationDataSource.GetFeedNotificationCallback {
            override fun onSuccess(feedNotificationResponse: List<FeedNotificationResponse>) {
                callback.onResult(feedNotificationResponse, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
            }
            override fun onFailed(reason: String) {
                showToast("알림 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(MissionCampaignException(reason))
            }
        })
    }
}

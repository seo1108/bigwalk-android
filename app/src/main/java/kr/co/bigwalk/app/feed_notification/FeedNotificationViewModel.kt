package kr.co.bigwalk.app.feed_notification

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import kr.co.bigwalk.app.data.feedNotification.repository.FeedNotificationPageDataSource
import kr.co.bigwalk.app.data.feedNotification.repository.FeedNotificationPageDataSourceFactory
import kr.co.bigwalk.app.feed.isEnableLike
import kr.co.bigwalk.app.feed_comment.FeedCommentActivity

class FeedNotificationViewModel(
    private val campaignId: Long,
    private val navigator: BaseNavigator
): ViewModel() {

    private val feedNotificationPageDataSourceFactory = FeedNotificationPageDataSourceFactory(campaignId)

    private val config =
        PagedList.Config.Builder().setPageSize(FeedNotificationPageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()

    val feedNotifications: LiveData<PagedList<FeedNotificationResponse>> =
        LivePagedListBuilder<Int, FeedNotificationResponse>(feedNotificationPageDataSourceFactory, config).build()


    fun presentComment(actionDonationHistoryId: Long) {
        val intent = Intent(navigator.getContext(), FeedCommentActivity::class.java).apply {
            val organizationId = PreferenceManager.getOrganization()
            val isEnable = isEnableLike(organizationId)
            putExtra(FeedCommentActivity.ACTION_DONATION_HISTORY_ID, actionDonationHistoryId)
            putExtra(FeedCommentActivity.ORGANIZATION_ID, organizationId)
            putExtra(FeedCommentActivity.IS_ENABLE_LIKE, isEnable)
        }
        navigator.getContext().startActivity(intent)
    }

    fun onclickBack() {
        navigator.finish()
    }
}
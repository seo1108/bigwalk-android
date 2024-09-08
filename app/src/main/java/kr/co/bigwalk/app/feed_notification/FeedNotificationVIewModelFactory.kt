package kr.co.bigwalk.app.feed_notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.CampaignException

class FeedNotificationVIewModelFactory(
    private val campaignId: Long,
    private val navigator: BaseNavigator
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FeedNotificationViewModel::class.java)) {
            FeedNotificationViewModel(campaignId, navigator) as T
        }else{
            navigator.finish()
            throw CampaignException("FeedLikeViewModel Not Found")
        }
    }
}
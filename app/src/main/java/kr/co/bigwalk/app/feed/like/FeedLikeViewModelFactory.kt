package kr.co.bigwalk.app.feed.like

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.CampaignException

class FeedLikeViewModelFactory(
    private val actionDonationHistoryId: Long,
    private val navigator: BaseNavigator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FeedLikeViewModel::class.java)) {
            FeedLikeViewModel(actionDonationHistoryId, navigator) as T
        }else{
            navigator.finish()
            throw CampaignException("FeedLikeViewModel Not Found")
        }
    }
}
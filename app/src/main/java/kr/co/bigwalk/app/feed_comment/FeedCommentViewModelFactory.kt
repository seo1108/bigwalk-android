package kr.co.bigwalk.app.feed_comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.CampaignException

class FeedCommentViewModelFactory(
    private val navigator: BaseNavigator?,
    private val actionDonationHistoryId: Long
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FeedCommentViewModel::class.java)) {
            FeedCommentViewModel(navigator, actionDonationHistoryId) as T
        }else{
            navigator?.finish()
            throw CampaignException("FeedCommentViewModel Not Found")
        }
    }
}
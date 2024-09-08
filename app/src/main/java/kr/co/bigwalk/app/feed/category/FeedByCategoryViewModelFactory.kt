package kr.co.bigwalk.app.feed.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.feed.FeedViewModel

class FeedByCategoryViewModelFactory(
    private val navigator: BaseNavigator?,
    private val campaignId: Long,
    private val type: FeedViewModel.FeedCategoryType,
    private val categoryId: Long,
    private val userId: Long
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FeedByCategoryViewModel::class.java)) {
            FeedByCategoryViewModel(navigator, campaignId, type, categoryId, userId) as T
        }else{
            navigator?.finish()
            throw CampaignException("FeedByCategoryViewModel Not Found")
        }
    }
}
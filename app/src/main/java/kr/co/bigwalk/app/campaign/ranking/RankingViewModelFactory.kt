package kr.co.bigwalk.app.campaign.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.CampaignException

class RankingViewModelFactory(
    private val campaignId: Long,
    private val navigator: BaseNavigator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RankingViewModel::class.java)) {
            RankingViewModel(campaignId, navigator) as T
        }else{
            navigator.finish()
            throw CampaignException("RankingViewModel Not Found")
        }
    }
}
package kr.co.bigwalk.app.campaign.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.exception.CampaignException

class RecentCampaignViewModelFactory (
    private val navigator: RecentCampaignNavigator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RecentCampaignViewModel::class.java)) {
            RecentCampaignViewModel(navigator) as T
        } else {
            navigator.finish()
            throw CampaignException("RecentCampaignViewModel Not Found")
        }
    }
}
package kr.co.bigwalk.app.community.funding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.exception.CampaignException

class SupportersCampaignPreviewViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SupportersCampaignPreviewViewModel::class.java)) {
            SupportersCampaignPreviewViewModel(crewCampaignId) as T
        } else {
            throw CampaignException("SupportersCampaignPreviewViewModel Not Found")
        }
    }
}
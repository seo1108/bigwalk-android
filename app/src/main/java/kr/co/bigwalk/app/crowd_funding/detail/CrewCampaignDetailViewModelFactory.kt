package kr.co.bigwalk.app.crowd_funding.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.exception.CampaignException

class CrewCampaignDetailViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CrewCampaignDetailViewModel::class.java)) {
            CrewCampaignDetailViewModel(crewCampaignId) as T
        } else {
            throw CampaignException("CrewCampaignDetailViewModel Not Found")
        }
    }
}
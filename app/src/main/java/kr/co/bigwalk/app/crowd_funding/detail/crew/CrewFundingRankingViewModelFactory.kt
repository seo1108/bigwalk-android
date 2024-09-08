package kr.co.bigwalk.app.crowd_funding.detail.crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CrewFundingRankingViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CrewFundingRankingViewModel::class.java)) {
            CrewFundingRankingViewModel(crewCampaignId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
package kr.co.bigwalk.app.crowd_funding.detail.crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CrewJoinFormViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CrewJoinFromViewModel::class.java)) {
            CrewJoinFromViewModel(crewCampaignId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
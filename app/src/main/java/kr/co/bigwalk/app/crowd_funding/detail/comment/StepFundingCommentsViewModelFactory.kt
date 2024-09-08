package kr.co.bigwalk.app.crowd_funding.detail.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.crowd_funding.detail.StepFundingCommentsViewModel
import kr.co.bigwalk.app.exception.CampaignException

class StepFundingCommentsViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StepFundingCommentsViewModel::class.java)) {
            StepFundingCommentsViewModel(crewCampaignId) as T
        } else {
            throw CampaignException("StepFundingCommentsViewModel Not Found")
        }
    }
}
package kr.co.bigwalk.app.community.funding.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.exception.CampaignException

class PreviewChallengeViewModelFactory(private val crewCampaignId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PreviewChallengeViewModel::class.java)) {
            PreviewChallengeViewModel(crewCampaignId) as T
        } else {
            throw CampaignException("PreviewChallengeViewModel Not Found")
        }
    }
}
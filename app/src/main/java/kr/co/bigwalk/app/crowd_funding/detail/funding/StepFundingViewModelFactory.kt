package kr.co.bigwalk.app.crowd_funding.detail.funding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.crowd_funding.detail.SendProgressItem

class StepFundingViewModelFactory(private val progressItem: SendProgressItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StepFundingViewModel::class.java)) {
            StepFundingViewModel(progressItem) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
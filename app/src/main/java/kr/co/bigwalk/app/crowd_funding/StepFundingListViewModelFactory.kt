package kr.co.bigwalk.app.crowd_funding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepFundingListViewModelFactory(private val contestId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StepFundingListViewModel::class.java)) {
            StepFundingListViewModel(contestId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
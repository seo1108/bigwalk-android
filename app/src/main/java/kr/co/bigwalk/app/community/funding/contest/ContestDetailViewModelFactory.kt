package kr.co.bigwalk.app.community.funding.contest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContestDetailViewModelFactory(private val contestId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ContestDetailViewModel::class.java)) {
            ContestDetailViewModel(contestId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
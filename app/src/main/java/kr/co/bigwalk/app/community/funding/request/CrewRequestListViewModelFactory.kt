package kr.co.bigwalk.app.community.funding.contest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.community.funding.request.CrewRequestListViewModel

class CrewRequestListViewModelFactory(private val groupId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CrewRequestListViewModel::class.java)) {
            CrewRequestListViewModel(groupId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
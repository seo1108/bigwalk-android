package kr.co.bigwalk.app.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GroupMemberListViewModelFactory(private val groupId: Long, private val requestJointCount: Int = 0) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GroupMemberListViewModel::class.java)) {
            GroupMemberListViewModel(groupId, requestJointCount) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
package kr.co.bigwalk.app.community.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GroupShareViewModelFactory(private val groupId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GroupShareViewModel::class.java)) {
            GroupShareViewModel(groupId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
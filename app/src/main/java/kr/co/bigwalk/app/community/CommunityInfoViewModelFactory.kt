package kr.co.bigwalk.app.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommunityInfoViewModelFactory(private val groupId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CommunityInfoViewModel::class.java)) {
            CommunityInfoViewModel(groupId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
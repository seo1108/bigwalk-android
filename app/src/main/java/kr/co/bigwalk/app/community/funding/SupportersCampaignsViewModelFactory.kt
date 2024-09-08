package kr.co.bigwalk.app.community.funding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SupportersCampaignsViewModelFactory(private val groupId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SupportersCampaignsViewModel::class.java)) {
            SupportersCampaignsViewModel(groupId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
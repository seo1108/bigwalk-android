package kr.co.bigwalk.app.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyCommunityListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyCommunityListViewModel::class.java)) {
            MyCommunityListViewModel() as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
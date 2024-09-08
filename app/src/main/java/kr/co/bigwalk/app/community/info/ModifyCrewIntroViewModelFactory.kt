package kr.co.bigwalk.app.community.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ModifyCrewIntroViewModelFactory(private val groupId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ModifyCrewIntroViewModel::class.java)) {
            ModifyCrewIntroViewModel(groupId) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
package kr.co.bigwalk.app.community.funding.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds

class CreateFundingViewModelFactory(private val requiredToCreateIds: RequiredToCreateIds) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CreateFundingViewModel::class.java)) {
            CreateFundingViewModel(requiredToCreateIds) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
package kr.co.bigwalk.app.crowd_funding.myfunding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.crowd_funding.FundingRewardResultItem

class MyFundingRewardViewModelFactory(private val item: FundingRewardResultItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyFundingRewardViewModel::class.java)) {
            MyFundingRewardViewModel(item) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
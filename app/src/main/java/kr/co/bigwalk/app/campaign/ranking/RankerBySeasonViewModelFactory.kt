package kr.co.bigwalk.app.campaign.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.exception.CampaignException

class RankerBySeasonViewModelFactory(private val seasonKey: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RankerBySeasonViewModel::class.java)) {
            RankerBySeasonViewModel(seasonKey) as T
        } else {
            throw CampaignException("RankingViewModel Not Found")
        }
    }
}
package kr.co.bigwalk.app.campaign.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.campaign.dto.RankerBySeasonResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.DebugLog

class RankerBySeasonViewModel(private val seasonKey: String) : ViewModel() {

    private val _rankerInfo = MutableLiveData<RankerBySeasonResponse>()
    val rankerInfo: LiveData<RankerBySeasonResponse> get() = _rankerInfo

    init {
        requestMyRanking()
    }

    private fun requestMyRanking() {
        CampaignRepository.getRankersBySeason(
            seasonKey = seasonKey,
            successCallback = { response ->
                _rankerInfo.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }
}
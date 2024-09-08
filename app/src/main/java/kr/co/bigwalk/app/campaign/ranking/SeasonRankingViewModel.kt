package kr.co.bigwalk.app.campaign.ranking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.campaign.dto.SeasonTopRankerResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.DebugLog

class SeasonRankingViewModel: ViewModel() {
    private val _topRankerInfo = MutableLiveData<List<SeasonTopRankerResponse>>()
    val topRankerInfo: LiveData<List<SeasonTopRankerResponse>> get() = _topRankerInfo

    init {
        getSeasonTopRankers()
    }

    private fun getSeasonTopRankers() {
        CampaignRepository.getSeasonTopRankers(
            successCallback = { response ->
                _topRankerInfo.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }
}
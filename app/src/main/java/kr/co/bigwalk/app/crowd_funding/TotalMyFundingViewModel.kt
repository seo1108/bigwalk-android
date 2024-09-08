package kr.co.bigwalk.app.crowd_funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingDescriptionResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingResponse
import kr.co.bigwalk.app.data.crowd_funding.repository.CommentByCrewCampaignDataSourceFactory
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.data.crowd_funding.repository.TotalMyFundingDataSourceFactory
import kr.co.bigwalk.app.util.DebugLog

class TotalMyFundingViewModel(private val contestId: Long?) : ViewModel() {
    private val _myFundingDescription = MutableLiveData<MyFundingDescriptionResponse>()
    val myFundingDescription: LiveData<MyFundingDescriptionResponse> get() = _myFundingDescription

    val factory = TotalMyFundingDataSourceFactory(contestId)

    val totalFundingList: LiveData<PagedList<TotalMyFundingResponse>> by lazy {
        LivePagedListBuilder(
            factory,
            CommentByCrewCampaignDataSourceFactory.pagedListConfig()
        ).build()
    }
    init {
        fetchMyFundingDescription()
    }

    private fun fetchMyFundingDescription() {
        if (contestId == null) return
        CrowdFundingRepository.getMyFundingDescription(
            contestId = contestId,
            successCallback = { response ->
                _myFundingDescription.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun invalidateDataSource(){
        factory.liveData.value?.invalidate()
    }
}
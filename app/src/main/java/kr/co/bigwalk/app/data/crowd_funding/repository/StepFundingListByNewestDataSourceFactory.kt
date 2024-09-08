package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByHottestResponse

class StepFundingListByNewestDataSourceFactory(private val contestId: Long) : DataSource.Factory<Int, FundingByHottestResponse>() {

    val liveData = MutableLiveData<StepFundingListByNewestDataSource>()

    override fun create(): DataSource<Int, FundingByHottestResponse> {
        val dataSource = StepFundingListByNewestDataSource(contestId)
        this.liveData.postValue(dataSource)
        return dataSource.map {
            it.toFundingByHottestResponse()
        }
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(CommentByCrewCampaignDataSource.PAGE_SIZE)
            .setPageSize(CommentByCrewCampaignDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}
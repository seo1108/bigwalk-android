package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByHottestResponse

class StepFundingListByHottestDataSourceFactory(private val contestId: Long) : DataSource.Factory<Int, FundingByHottestResponse>() {

    val liveData = MutableLiveData<StepFundingListByHottestDataSource>()

    override fun create(): DataSource<Int, FundingByHottestResponse> {
        val dataSource = StepFundingListByHottestDataSource(contestId)
        this.liveData.postValue(dataSource)
        return dataSource
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(CommentByCrewCampaignDataSource.PAGE_SIZE)
            .setPageSize(CommentByCrewCampaignDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}
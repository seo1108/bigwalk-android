package kr.co.bigwalk.app.data.funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignRankingResponse

class CrewCampaignRankingDataSourceFactory(private val crewCampaignId: Long) : DataSource.Factory<Int, CrewCampaignRankingResponse>() {

    val liveData = MutableLiveData<CrewCampaignRankingDataSource>()

    override fun create(): DataSource<Int, CrewCampaignRankingResponse> {
        val dataSource = CrewCampaignRankingDataSource(crewCampaignId)
        liveData.postValue(dataSource)
        return dataSource
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(CrewCampaignRankingDataSource.PAGE_SIZE)
            .setPageSize(CrewCampaignRankingDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}
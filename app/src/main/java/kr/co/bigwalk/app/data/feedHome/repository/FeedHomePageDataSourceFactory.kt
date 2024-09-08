package kr.co.bigwalk.app.data.feedHome.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedHome.dto.MissionCampaignResponse


class FeedHomePageDataSourceFactory: DataSource.Factory<Int, MissionCampaignResponse>() {

    private val missionCampaignsLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, MissionCampaignResponse>>()

    override fun create(): DataSource<Int, MissionCampaignResponse> {
        val feedHomePageDataSource = FeedHomePageDataSource()
        missionCampaignsLiveDataSource.postValue(feedHomePageDataSource)
        return feedHomePageDataSource
    }
}
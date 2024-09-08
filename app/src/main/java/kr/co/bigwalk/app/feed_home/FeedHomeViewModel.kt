package kr.co.bigwalk.app.feed_home

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.feedHome.dto.MissionCampaignResponse
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomePageDataSource
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomePageDataSourceFactory

class FeedHomeViewModel {

    private val missionCampaignDataSourceFactory =
        FeedHomePageDataSourceFactory()
    private val config =
        PagedList.Config.Builder().setPageSize(FeedHomePageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()
    val missionCampaignsResponse: LiveData<PagedList<MissionCampaignResponse>> =
        LivePagedListBuilder<Int, MissionCampaignResponse>(
            missionCampaignDataSourceFactory,
            config
        ).build()

}
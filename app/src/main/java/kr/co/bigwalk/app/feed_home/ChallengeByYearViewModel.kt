package kr.co.bigwalk.app.feed_home

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearPagingResponse
import kr.co.bigwalk.app.data.feedHome.repository.ChallengeYearPageDataSource
import kr.co.bigwalk.app.data.feedHome.repository.ChallengeYearPageDataSource.Companion.PAGE_SIZE
import kr.co.bigwalk.app.data.feedHome.repository.ChallengeYearPageDataSourceFactory

class ChallengeByYearViewModel(type: String, year: String) {

    private val challengeByYearDataSourceFactory =
        ChallengeYearPageDataSourceFactory(type, year)
    private val config =
        PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()
    val challengeByYear: LiveData<PagedList<ChallengeYearPagingResponse>> =
        LivePagedListBuilder<Int, ChallengeYearPagingResponse>(
            challengeByYearDataSourceFactory,
            config
        ).build()

    fun invalidateDataSource(){
        challengeByYearDataSourceFactory.invalidateDataSource()
    }
}
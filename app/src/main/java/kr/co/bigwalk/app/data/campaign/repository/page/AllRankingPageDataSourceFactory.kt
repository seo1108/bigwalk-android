package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse

class AllRankingPageDataSourceFactory : DataSource.Factory<Int, RankingResponse>() {

    private val rankingLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, RankingResponse>>()

    override fun create(): DataSource<Int, RankingResponse> {
        val rankingPageDataSource = AllRankingPageDataSource()
        rankingLiveDataSource.postValue(rankingPageDataSource)
        return rankingPageDataSource
    }
}
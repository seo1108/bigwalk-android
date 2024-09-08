package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.RankData

class RankPageDataSourceFactory(
    private val rankCategory: String,
    private val rankInfoListener: RankInfoListener
) : DataSource.Factory<Int, RankData>() {
    val rankLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, RankData>>()

    override fun create(): DataSource<Int, RankData> {
        val rankPageDataSource = RankPageDataSource(rankCategory, rankInfoListener)
        rankLiveDataSource.postValue(rankPageDataSource)
        return rankPageDataSource
    }
}
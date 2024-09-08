package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.ContentResponse

class CommentByCrewCampaignDataSourceFactory(
    private val crewCampaignId: Long,
    private val commentCountCallback: (Int) -> Unit
) : DataSource.Factory<Int, ContentResponse>() {

    val rankingLiveDataSource = MutableLiveData<CommentByCrewCampaignDataSource>()

    override fun create(): DataSource<Int, ContentResponse> {
        val dataSource = CommentByCrewCampaignDataSource(crewCampaignId, commentCountCallback)
        rankingLiveDataSource.postValue(dataSource)
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
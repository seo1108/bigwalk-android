package kr.co.bigwalk.app.data.feed.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.feed.FeedViewModel

class FeedPageDataSourceFactory(
    val campaignId: Long,
    val type: FeedViewModel.FeedCategoryType,
    val departmentId: Long?,
    val actionId: Long?,
    val userId: Long
) : DataSource.Factory<Int, Feed>() {

    val feedsLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Feed>>()

    override fun create(): DataSource<Int, Feed> {
        val feedPageDataSource = FeedPageDataSource(campaignId, type, departmentId, actionId, userId)
        feedsLiveDataSource.postValue(feedPageDataSource)
        return feedPageDataSource
    }
}
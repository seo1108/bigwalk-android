package kr.co.bigwalk.app.data.feedComment.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse

class FeedCommentPageDataSourceFactory(
    private val actionDonationHistoryId: Long
): DataSource.Factory<Int, FeedCommentResponse>() {

    private val commentsLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, FeedCommentResponse>>()

    override fun create(): DataSource<Int, FeedCommentResponse> {
        val feedCommentPageDataSource = FeedCommentPageDataSource(actionDonationHistoryId)
        commentsLiveDataSource.postValue(feedCommentPageDataSource)
        return feedCommentPageDataSource
    }
}
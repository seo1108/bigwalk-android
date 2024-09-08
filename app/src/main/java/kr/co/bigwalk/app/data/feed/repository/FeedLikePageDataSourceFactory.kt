package kr.co.bigwalk.app.data.feed.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse

class FeedLikePageDataSourceFactory(private val actionDonationHistoryId: Long) : DataSource.Factory<Int, UserProfileResponse>() {

    private val likedUsersLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, UserProfileResponse>>()

    override fun create(): DataSource<Int, UserProfileResponse> {
        val pageDataSource = FeedLikePageDataSource(actionDonationHistoryId)
        likedUsersLiveDataSource.postValue(pageDataSource)
        return pageDataSource
    }
}
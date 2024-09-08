package kr.co.bigwalk.app.feed.like

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.feed.repository.FeedLikePageDataSource.Companion.PAGE_SIZE
import kr.co.bigwalk.app.data.feed.repository.FeedLikePageDataSourceFactory
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse

class FeedLikeViewModel(
    private val actionDonationHistoryId: Long,
    private val navigator: BaseNavigator
) : ViewModel() {

    private val feedLikePageDataSourceFactory = FeedLikePageDataSourceFactory(actionDonationHistoryId)
    private val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()
    val likedUsers: LiveData<PagedList<UserProfileResponse>> = LivePagedListBuilder<Int, UserProfileResponse>(feedLikePageDataSourceFactory, config).build()

    var isLikeMe: MutableLiveData<Boolean> = MutableLiveData(false)
    var myProfile: MutableLiveData<UserProfileResponse> = MutableLiveData()
    var profilePath: MutableLiveData<String> = MutableLiveData()
    var departmentName: MutableLiveData<String> = MutableLiveData()
    var userName: MutableLiveData<String> = MutableLiveData()

    fun finish() {
        navigator.finish()
    }
}
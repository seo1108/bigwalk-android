package kr.co.bigwalk.app.feed.category

import android.content.Intent
import android.os.Handler
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feed.dto.FeedListResponse
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.data.feed.repository.FeedPageDataSourceFactory
import kr.co.bigwalk.app.data.feed.repository.FeedRepository
import kr.co.bigwalk.app.data.funding.dto.ContestResponse
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.feed.FeedPostViewModel
import kr.co.bigwalk.app.feed.FeedViewModel
import kr.co.bigwalk.app.feed.like.FeedLikeActivity
import kr.co.bigwalk.app.feed_mypage.FeedMyPageActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FeedByCategoryViewModel(
    private val navigator: BaseNavigator?,
    private val campaignId: Long,
    private val type: FeedViewModel.FeedCategoryType,
    private val categoryId: Long,
    private val userId: Long
): FeedPostViewModel(navigator) {
   /* private var pageSize = 10
    private val config = PagedList.Config.Builder().setPageSize(pageSize).setEnablePlaceholders(true).build()

    private val feedDataSourceFactory: FeedPageDataSourceFactory = FeedPageDataSourceFactory(campaignId, type, categoryId, null, userId)
    val feedsLiveData: LiveData<PagedList<Feed>> = LivePagedListBuilder(feedDataSourceFactory, config).build()*/

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
}
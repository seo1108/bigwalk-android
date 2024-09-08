package kr.co.bigwalk.app.feed_comment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.AppDatabase
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.data.feedComment.repository.*
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.feed.FeedPostViewModel
import kr.co.bigwalk.app.util.showToast

class FeedCommentViewModel(
    private val navigator: BaseNavigator?,
    private val actionDonationHistoryId: Long
): FeedPostViewModel(navigator) {

    private val feedCommentPageDataSourceFactory = FeedCommentPageDataSourceFactory(actionDonationHistoryId)
    private val config = PagedList.Config.Builder().setPageSize(FeedCommentPageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()
    val comments: LiveData<PagedList<FeedCommentResponse>> = LivePagedListBuilder<Int, FeedCommentResponse>(feedCommentPageDataSourceFactory, config).build()

//    private val commentBoundaryRepository = CommentBoundaryRepository(actionDonationHistoryId).getCommentList()
//    val comments = commentBoundaryRepository.pagedList
//    val networkState = commentBoundaryRepository.networkState
//    val refreshState = commentBoundaryRepository.refreshState
//
//    fun refresh() {
//        commentBoundaryRepository.refresh.invoke()
//    }

    val profilePath: LiveData<String?> = MutableLiveData(PreferenceManager.getProfilePath())

    val inputText = MutableLiveData<String>()

    val hideKeyboard = MutableLiveData<Boolean>()

    val feedInfo = MutableLiveData<Feed>()
    val feedError = MutableLiveData<Boolean>(false)

    fun fetchFeedInfo() {
        FeedCommentRepository.getFeedInfo(actionDonationHistoryId, object : FeedCommentDataSource.GetFeedInfoCallback {
            override fun onSuccess(feedInfoResult: FeedInfoResult) {
                when (feedInfoResult) {
                    is FeedInfoResult.Success -> {
                        feedInfo.value = feedInfoResult.data
                    }
                    is FeedInfoResult.NotFound -> {
                        feedError.value = true
                    }
                    else -> {
                        feedError.value = true
                    }
                }
            }

            override fun onFailed(feedInfoResult: FeedInfoResult) {
//                showToast("게시물 정보 가져오기 실패.")
                feedError.value = true
            }
        })
    }

    fun postComment() {
        if (inputText.value.isNullOrEmpty()) return
        FeedCommentRepository.postFeedComment(actionDonationHistoryId, inputText.value!!, object : FeedCommentDataSource.PostFeedCommentCallback {
            override fun onSuccess(comment: FeedCommentResponse) {
                CoroutineScope(Dispatchers.IO).launch {
                    val feed = feedInfo?.value
                    feed?.let {
                        it.commentCount += 1
                        PreferenceManager.saveFeedCommentCount(it.actionDonationHistoryId, it.commentCount)
                        withContext(Dispatchers.Main) {
                            feedInfo.value = it
                            FeedManager.commentFeed.value = it
                        }
                    }
                    AppDatabase(BigwalkApplication.context!!).commentDao().insertComment(comment)

                }
            }
            override fun onFailed(reason: String) {
                showToast("댓글 전송 실패.")
            }
        })
        inputText.value = ""
        hideKeyboard.value = true
    }

    fun deleteComment(position: Int, commentId: Long) {
        FeedCommentRepository.deleteFeedComment(actionDonationHistoryId, commentId, object : FeedCommentDataSource.DeleteFeedCommentCallback {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSuccess() {
                CoroutineScope(Dispatchers.IO).launch {
                    val feed = feedInfo?.value
                    feed?.let {
                        it.commentCount -= 1
                        PreferenceManager.saveFeedCommentCount(it.actionDonationHistoryId, it.commentCount)
                        withContext(Dispatchers.Main) {
                            feedInfo.value = it
                            FeedManager.commentFeed.value = it
                        }
                        AppDatabase(BigwalkApplication.context!!).commentDao()
                            .deleteComment(it.actionDonationHistoryId, commentId)
                    }

                }
            }
            override fun onFailed(reason: String) {
                showToast("댓글 삭제 실패.")
            }
        })
    }
}
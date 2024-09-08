package kr.co.bigwalk.app.data.feedComment.repository.inDb

import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.AppDatabase
import kr.co.bigwalk.app.data.PagingRequestHelper
import kr.co.bigwalk.app.data.createStatusLiveData
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.data.feedComment.repository.FeedCommentDataSource
import kr.co.bigwalk.app.data.feedComment.repository.FeedCommentRepository
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import java.util.concurrent.Executors

class CommentBoundaryCallback(
    private val actionDonationHistoryId: Long
): PagedList.BoundaryCallback<FeedCommentResponse>() {

    private val helper = PagingRequestHelper(Executors.newSingleThreadExecutor())
    val networkState = helper.createStatusLiveData()

    private var helperCallback: PagingRequestHelper.Request.Callback? = null

    private var page = 0
    private val size = 10

    private val callback = object : FeedCommentDataSource.GetFeedCommentCallback {
        override fun onSuccess(comments: List<FeedCommentResponse>) {
            CoroutineScope(Dispatchers.IO).launch  {
                AppDatabase(BigwalkApplication.context!!).runInTransaction {
                    AppDatabase(BigwalkApplication.context!!).commentDao().insertCommentList(comments)
                    helperCallback?.recordSuccess()
                }
            }
        }
        override fun onFailed(reason: String) {
            showToast("댓글 목록을 불러올 수 없습니다. 다시 시도해주세요.")
            DebugLog.e(StoryException(reason))
            helperCallback?.recordFailure(Exception(reason))
        }
    }

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            requestCommentList(actionDonationHistoryId, page, size, callback)
            helperCallback = it
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: FeedCommentResponse) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            requestCommentList(actionDonationHistoryId, page++, size, callback)
            helperCallback = it
        }
    }


    companion object {
        fun requestCommentList(
            actionDonationHistoryId: Long,
            page: Int,
            size: Int,
            callback: FeedCommentDataSource.GetFeedCommentCallback
        ) {
            FeedCommentRepository.getFeedComments(actionDonationHistoryId, page, size, callback)
        }
    }
}
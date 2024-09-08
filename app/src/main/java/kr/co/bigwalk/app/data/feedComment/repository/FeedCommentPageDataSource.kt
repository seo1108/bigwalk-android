package kr.co.bigwalk.app.data.feedComment.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.exception.CommentException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedCommentPageDataSource(
    private val actionDonationHistoryId: Long
): PageKeyedDataSource<Int, FeedCommentResponse>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, FeedCommentResponse>
    ) {
        FeedCommentRepository.getFeedComments(actionDonationHistoryId, FIRST_PAGE, PAGE_SIZE, object : FeedCommentDataSource.GetFeedCommentCallback {
            override fun onSuccess(comments: List<FeedCommentResponse>) {
                callback.onResult(comments, null, FIRST_PAGE + 1)
            }

            override fun onFailed(reason: String) {
                showToast("댓글 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CommentException(reason))
            }
        })
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, FeedCommentResponse>
    ) {
        FeedCommentRepository.getFeedComments(actionDonationHistoryId, params.key, params.requestedLoadSize, object : FeedCommentDataSource.GetFeedCommentCallback {
            override fun onSuccess(comments: List<FeedCommentResponse>) {
                callback.onResult(comments, if (params.key > FIRST_PAGE) params.key - 1 else null)
            }

            override fun onFailed(reason: String) {
                showToast("댓글 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CommentException(reason))
            }
        })
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, FeedCommentResponse>
    ) {
        FeedCommentRepository.getFeedComments(actionDonationHistoryId, params.key, params.requestedLoadSize, object : FeedCommentDataSource.GetFeedCommentCallback {
            override fun onSuccess(comments: List<FeedCommentResponse>) {
                callback.onResult(comments, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
            }

            override fun onFailed(reason: String) {
                showToast("댓글 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CommentException(reason))
            }
        })
    }
}
package kr.co.bigwalk.app.data.feedComment.repository

import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import java.lang.Exception

interface FeedCommentDataSource {
    interface GetFeedCommentCallback {
        fun onSuccess(comments : List<FeedCommentResponse>)
        fun onFailed(reason: String)
    }
    fun getFeedComments(actionDonationHistoryId: Long,
                        page: Int,
                        size: Int,
                        callback: GetFeedCommentCallback)

    interface PostFeedCommentCallback {
        fun onSuccess(comment: FeedCommentResponse)
        fun onFailed(reason: String)
    }
    fun postFeedComment(actionDonationHistoryId: Long,
                        comment: String,
                        callback: PostFeedCommentCallback)

    interface DeleteFeedCommentCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }
    fun deleteFeedComment(actionDonationHistoryId: Long,
                          id: Long,
                          callback: DeleteFeedCommentCallback)

    interface GetFeedInfoCallback {
        fun onSuccess(feedInfoResult: FeedInfoResult)
        fun onFailed(feedInfoResult: FeedInfoResult)
    }
    fun getFeedInfo(actionDonationHistoryId: Long,
                    callback: GetFeedInfoCallback)
}

sealed class FeedInfoResult {
    data class Success(val data: Feed) : FeedInfoResult()
    data class NotFound(val msg: String) : FeedInfoResult()
    data class Error(val msg: String) : FeedInfoResult()
}
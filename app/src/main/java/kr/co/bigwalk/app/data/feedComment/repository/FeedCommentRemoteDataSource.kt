package kr.co.bigwalk.app.data.feedComment.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentListResponse
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedCommentRemoteDataSource: FeedCommentDataSource {

    override fun getFeedComments(
        actionDonationHistoryId: Long,
        page: Int,
        size: Int,
        callback: FeedCommentDataSource.GetFeedCommentCallback
    ) {
        RemoteApiManager.getService().getFeedComments(actionDonationHistoryId, page, size).enqueue(object :
            Callback<BaseResponse<FeedCommentListResponse>> {

            override fun onResponse(
                call: Call<BaseResponse<FeedCommentListResponse>>,
                response: Response<BaseResponse<FeedCommentListResponse>>
            ) {
                when (response.code()) {
                    200 -> response.body()?.data?.let { callback.onSuccess(it.content) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse<FeedCommentListResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun postFeedComment(
        actionDonationHistoryId: Long,
        comment: String,
        callback: FeedCommentDataSource.PostFeedCommentCallback
    ) {
        RemoteApiManager.getService().postFeedComment(actionDonationHistoryId, comment).enqueue(object :
            Callback<FeedCommentResponse> {

            override fun onResponse(call: Call<FeedCommentResponse>, response: Response<FeedCommentResponse>) {
                when (response.code()) {
                    201 -> response.body()?.let { callback.onSuccess(it) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedCommentResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun deleteFeedComment(
        actionDonationHistoryId: Long,
        id: Long,
        callback: FeedCommentDataSource.DeleteFeedCommentCallback
    ) {
        RemoteApiManager.getService().deleteFeedComment(actionDonationHistoryId, id).enqueue(object :
            Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    204 -> callback.onSuccess()
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getFeedInfo(
        actionDonationHistoryId: Long,
        callback: FeedCommentDataSource.GetFeedInfoCallback
    ) {
        RemoteApiManager.getService().getFeedInfo(actionDonationHistoryId).enqueue(object :
            Callback<Feed> {
            override fun onResponse(call: Call<Feed>, response: Response<Feed>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(FeedInfoResult.Success(it)) }
                    410, 500 -> callback.onFailed(FeedInfoResult.NotFound(response.errorBody()?.string()!!))
                    else -> callback.onFailed(FeedInfoResult.NotFound(response.errorBody()?.string()!!))
                }
            }
            override fun onFailure(call: Call<Feed>, t: Throwable) {
                callback.onFailed(FeedInfoResult.Error(t.localizedMessage))
            }
        })
    }

}
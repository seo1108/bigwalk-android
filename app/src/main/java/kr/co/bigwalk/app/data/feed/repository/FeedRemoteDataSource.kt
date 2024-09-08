package kr.co.bigwalk.app.data.feed.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.DeclareFeedRequest
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.feed.dto.ChallengeDetailResponse
import kr.co.bigwalk.app.data.feed.dto.ChallengeImageResponse
import kr.co.bigwalk.app.data.feed.dto.FeedInfoResponse
import kr.co.bigwalk.app.data.feed.dto.FeedListResponse
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearResponse
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomeDataSource
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedRemoteDataSource : FeedDataSource {

    override fun getFeedInfo(callback: FeedDataSource.GetFeedInfoCallback) {
        RemoteApiManager.getService().getFeedInfo().enqueue(object : Callback<FeedInfoResponse>{
            override fun onResponse(call: Call<FeedInfoResponse>, response: Response<FeedInfoResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedInfoResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getFeedList(page: Int, size: Int, callback: FeedDataSource.GetFeedListCallback) {
        RemoteApiManager.getService().getFeedList(page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getMyFeedList(campaignId: Long, page: Int, size: Int, callback: FeedDataSource.GetFeedListCallback) {
        RemoteApiManager.getService().getMyFeedList(campaignId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getUserFeedList(
        campaignId: Long,
        userId: Long,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListCallback
    ) {
        RemoteApiManager.getService().getUserFeedList(campaignId, userId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getFeedList(
        campaignId: Long,
        departmentId: Long?,
        actionId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListCallback
    ) {
        RemoteApiManager.getService().getFeedListByCampaign(campaignId, departmentId, actionId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun likeFeed(actionHistoryId: Long, callback: FeedDataSource.LikeFeedCallback) {
        RemoteApiManager.getService().likeFeed(actionHistoryId).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> callback.onSuccess()
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun unlikeFeed(actionHistoryId: Long, callback: FeedDataSource.LikeFeedCallback) {
        RemoteApiManager.getService().unlikeFeed(actionHistoryId).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> callback.onSuccess()
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun deleteFeed(actionHistoryId: Long, callback: FeedDataSource.DeleteFeedCallback) {
        RemoteApiManager.getService().deleteFeed(actionHistoryId).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    204 -> callback.onSuccess()
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getLikedUsers(actionHistoryId: Long, page: Int, size: Int, callback: FeedDataSource.LikedUsersCallback) {
        RemoteApiManager.getService().getLikedUsers(actionHistoryId, page, size).enqueue(object : Callback<List<UserProfileResponse>> {
            override fun onFailure(call: Call<List<UserProfileResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<UserProfileResponse>>, response: Response<List<UserProfileResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(it) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun declareFeed(actionHistoryId: Long, blameMessage: String, callback: FeedDataSource.DeclareFeedCallback) {
        RemoteApiManager.getBlameApi().declareFeed(DeclareFeedRequest(actionHistoryId, blameMessage)).enqueue(object : Callback<BaseResponse<Nothing>> {
            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                when(response.body()?.result) {
                    Result.SUCCESS -> {
                        response.body()?.let { callback.onSuccess(it) }
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let { callback.onFailed(it) }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

        })
    }

    override fun getChallengeDetail(challengeId: Long, callback: FeedDataSource.GetChallengeDetailCallback) {
        RemoteApiManager.getService().getChallengeDetail(challengeId).enqueue(object : Callback<ChallengeDetailResponse>{
            override fun onResponse(call: Call<ChallengeDetailResponse>, response: Response<ChallengeDetailResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<ChallengeDetailResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getChallengeImage(
        challengeId: Long,
        type: String,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetChallengeImageCallback
    ) {
        RemoteApiManager.getService().getChallengeImages(challengeId, type, page, size).enqueue(object : Callback<ChallengeImageResponse>{
            override fun onResponse(call: Call<ChallengeImageResponse>, response: Response<ChallengeImageResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<ChallengeImageResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getHotFeedListByChallenge(
        challengeId: Long,
        id: Long,
        departmentId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetHotFeedListByChallengeCallback
    ) {
        RemoteApiManager.getService().getHotFeedListByChallenge(challengeId, id, departmentId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getRecentFeedListByChallenge(
        challengeId: Long,
        id: Long,
        departmentId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetRecentFeedListByChallengeCallback
    ) {
        RemoteApiManager.getService().getRecentFeedListByChallenge(challengeId, id, departmentId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getMyFeedListByChallenge(
        challengeId: Long,
        id: Long,
        departmentId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetMyFeedListByChallengeCallback
    ) {
        RemoteApiManager.getService().getMyFeedListByChallenge(challengeId, id, departmentId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getFeedListByChallenge(
        challengeId: Long,
        id: Long,
        type: String,
        departmentId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListByChallengeCallback
    ) {
        RemoteApiManager.getService().getFeedListByChallenge(challengeId, id, type, departmentId, page, size).enqueue(object : Callback<FeedListResponse>{
            override fun onResponse(call: Call<FeedListResponse>, response: Response<FeedListResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<FeedListResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }
}

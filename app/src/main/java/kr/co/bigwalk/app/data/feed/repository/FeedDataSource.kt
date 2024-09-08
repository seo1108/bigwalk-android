package kr.co.bigwalk.app.data.feed.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.feed.dto.ChallengeDetailResponse
import kr.co.bigwalk.app.data.feed.dto.ChallengeImageResponse
import kr.co.bigwalk.app.data.feed.dto.FeedInfoResponse
import kr.co.bigwalk.app.data.feed.dto.FeedListResponse
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse

interface FeedDataSource {
    interface GetFeedInfoCallback {
        fun onSuccess(feedInfo : FeedInfoResponse)
        fun onFailed(reason: String)
    }
    fun getFeedInfo(callback: GetFeedInfoCallback)


    interface GetFeedListCallback {
        fun onSuccess(feedList : FeedListResponse)
        fun onFailed(reason: String)
    }
    fun getFeedList(page: Int, size: Int, callback: GetFeedListCallback)
    fun getFeedList(campaignId: Long, departmentId: Long?, actionId: Long?, page: Int, size: Int, callback: GetFeedListCallback)
    fun getMyFeedList(campaignId: Long, page: Int, size: Int, callback: GetFeedListCallback)
    fun getUserFeedList(campaignId: Long, userId: Long, page: Int, size: Int, callback: GetFeedListCallback)

    interface LikeFeedCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }
    fun likeFeed(actionHistoryId: Long, callback: LikeFeedCallback)
    fun unlikeFeed(actionHistoryId: Long, callback: LikeFeedCallback)

    interface DeleteFeedCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }
    fun deleteFeed(actionHistoryId: Long, callback: DeleteFeedCallback)

    interface LikedUsersCallback {
        fun onSuccess(users: List<UserProfileResponse>)
        fun onFailed(reason: String)
    }
    fun getLikedUsers(actionHistoryId: Long, page: Int, size: Int, callback: LikedUsersCallback)

    interface DeclareFeedCallback {
        fun onSuccess(response: BaseResponse<Nothing>)
        fun onFailed(reason: String)
    }
    fun declareFeed(actionHistoryId: Long, blameMessage: String, callback: DeclareFeedCallback)

    interface GetChallengeDetailCallback {
        fun onSuccess(response: ChallengeDetailResponse)
        fun onFailed(reason: String)
    }
    fun getChallengeDetail(challengeId: Long, callback: GetChallengeDetailCallback)

    interface GetChallengeImageCallback {
        fun onSuccess(response: ChallengeImageResponse)
        fun onFailed(reason: String)
    }
    fun getChallengeImage(challengeId: Long, type: String, page: Int, size: Int, callback: GetChallengeImageCallback)

    interface GetHotFeedListByChallengeCallback {
        fun onSuccess(response: FeedListResponse)
        fun onFailed(reason: String)
    }
    fun getHotFeedListByChallenge(challengeId: Long, id: Long, departmentId: Long?, page: Int, size: Int, callback: GetHotFeedListByChallengeCallback)

    interface GetRecentFeedListByChallengeCallback {
        fun onSuccess(response: FeedListResponse)
        fun onFailed(reason: String)
    }
    fun getRecentFeedListByChallenge(challengeId: Long, id: Long, departmentId: Long?, page: Int, size: Int, callback: GetRecentFeedListByChallengeCallback)

    interface GetMyFeedListByChallengeCallback {
        fun onSuccess(response: FeedListResponse)
        fun onFailed(reason: String)
    }
    fun getMyFeedListByChallenge(challengeId: Long, id: Long, departmentId: Long?, page: Int, size: Int, callback: GetMyFeedListByChallengeCallback)

    interface GetFeedListByChallengeCallback {
        fun onSuccess(response: FeedListResponse)
        fun onFailed(reason: String)
    }
    fun getFeedListByChallenge(challengeId: Long, id: Long, type: String, departmentId: Long?, page: Int, size: Int, callback: GetFeedListByChallengeCallback)
}
package kr.co.bigwalk.app.data.feed.repository

import kr.co.bigwalk.app.data.feed.dto.Feed

object FeedRepository : FeedDataSource {

    private val FeedDataSource : FeedDataSource

    init {
        @Suppress("ConstantConditionIf")
        FeedDataSource = FeedRemoteDataSource
    }

    override fun getFeedInfo(callback: FeedDataSource.GetFeedInfoCallback) {
        FeedDataSource.getFeedInfo(callback)
    }

    override fun getFeedList(page: Int, size: Int, callback: FeedDataSource.GetFeedListCallback) {
        FeedDataSource.getFeedList(page, size, callback)
    }

    override fun getFeedList(
        campaignId: Long,
        departmentId: Long?,
        actionId: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListCallback
    ) {
        FeedDataSource.getFeedList(campaignId, departmentId, actionId, page, size, callback)
    }

    override fun getMyFeedList(
        campaignId: Long,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListCallback
    ) {
        FeedDataSource.getMyFeedList(campaignId, page, size, callback)
    }

    override fun getUserFeedList(
        campaignId: Long,
        userId: Long,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListCallback
    ) {
        FeedDataSource.getUserFeedList(campaignId, userId, page, size, callback)
    }


    override fun likeFeed(actionHistoryId: Long, callback: FeedDataSource.LikeFeedCallback) {
        FeedDataSource.likeFeed(actionHistoryId, callback)
    }

    override fun unlikeFeed(actionHistoryId: Long, callback: FeedDataSource.LikeFeedCallback) {
        FeedDataSource.unlikeFeed(actionHistoryId, callback)
    }

    override fun deleteFeed(actionHistoryId: Long, callback: FeedDataSource.DeleteFeedCallback) {
        FeedDataSource.deleteFeed(actionHistoryId, callback)
    }

    override fun getLikedUsers(actionHistoryId: Long,
                               page: Int,
                               size: Int,
                               callback: FeedDataSource.LikedUsersCallback) {
        FeedDataSource.getLikedUsers(actionHistoryId, page, size, callback)
    }

    override fun declareFeed(actionHistoryId: Long, blameMessage: String, callback: FeedDataSource.DeclareFeedCallback) {
        FeedDataSource.declareFeed(actionHistoryId, blameMessage, callback)
    }

    override fun getChallengeDetail(
        challengeId: Long,
        callback: FeedDataSource.GetChallengeDetailCallback
    ) {
        FeedDataSource.getChallengeDetail(challengeId, callback)
    }

    override fun getChallengeImage(
        challengeId: Long,
        type: String,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetChallengeImageCallback
    ) {
        FeedDataSource.getChallengeImage(challengeId, type, page, size, callback)
    }

    override fun getHotFeedListByChallenge(
        challengeId: Long,
        id: Long,
        department: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetHotFeedListByChallengeCallback
    ) {
        FeedDataSource.getHotFeedListByChallenge(challengeId, id, department, page, size, callback)
    }

    override fun getRecentFeedListByChallenge(
        challengeId: Long,
        id: Long,
        department: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetRecentFeedListByChallengeCallback
    ) {
        FeedDataSource.getRecentFeedListByChallenge(challengeId, id, department, page, size, callback)
    }

    override fun getMyFeedListByChallenge(
        challengeId: Long,
        id: Long,
        department: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetMyFeedListByChallengeCallback
    ) {
        FeedDataSource.getMyFeedListByChallenge(challengeId, id, department, page, size, callback)
    }

    override fun getFeedListByChallenge(
        challengeId: Long,
        id: Long,
        type: String,
        department: Long?,
        page: Int,
        size: Int,
        callback: FeedDataSource.GetFeedListByChallengeCallback
    ) {
        FeedDataSource.getFeedListByChallenge(challengeId, id, type, department, page, size, callback)
    }
}
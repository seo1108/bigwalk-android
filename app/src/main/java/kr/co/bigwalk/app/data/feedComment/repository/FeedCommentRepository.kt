package kr.co.bigwalk.app.data.feedComment.repository

object FeedCommentRepository: FeedCommentDataSource {

    private val feedCommentRemoteDataSource = FeedCommentRemoteDataSource

    override fun getFeedComments(
        actionDonationHistoryId: Long,
        page: Int,
        size: Int,
        callback: FeedCommentDataSource.GetFeedCommentCallback
    ) {
        feedCommentRemoteDataSource.getFeedComments(actionDonationHistoryId, page, size, callback)
    }

    override fun postFeedComment(
        actionDonationHistoryId: Long,
        comment: String,
        callback: FeedCommentDataSource.PostFeedCommentCallback
    ) {
        feedCommentRemoteDataSource.postFeedComment(actionDonationHistoryId, comment, callback)
    }

    override fun deleteFeedComment(
        actionDonationHistoryId: Long,
        id: Long,
        callback: FeedCommentDataSource.DeleteFeedCommentCallback
    ) {
        feedCommentRemoteDataSource.deleteFeedComment(actionDonationHistoryId, id, callback)
    }

    override fun getFeedInfo(
        actionDonationHistoryId: Long,
        callback: FeedCommentDataSource.GetFeedInfoCallback
    ) {
        feedCommentRemoteDataSource.getFeedInfo(actionDonationHistoryId, callback)
    }

}
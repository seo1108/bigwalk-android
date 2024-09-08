package kr.co.bigwalk.app.data.feedComment.dto

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommentList(commentList: List<FeedCommentResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComment(comment: FeedCommentResponse)

    @Query("SELECT * FROM comment WHERE actionDonationHistoryId = :actionDonationHistoryId ORDER BY actionDonationHistoryId ASC")
    fun getComment(actionDonationHistoryId: Long): DataSource.Factory<Int, FeedCommentResponse>

    @Query("SELECT * FROM comment WHERE actionDonationHistoryId = :actionDonationHistoryId ORDER BY actionDonationHistoryId ASC")
    fun getComments(actionDonationHistoryId: Long): LiveData<List<FeedCommentResponse>>

    @Query("DELETE FROM comment WHERE actionDonationHistoryId = :actionDonationHistoryId AND id = :commentId")
    fun deleteComment(actionDonationHistoryId: Long, commentId: Long)
}
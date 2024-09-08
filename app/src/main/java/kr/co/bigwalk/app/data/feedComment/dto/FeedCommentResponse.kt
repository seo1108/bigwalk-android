package kr.co.bigwalk.app.data.feedComment.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.co.bigwalk.app.data.organization.Department

data class FeedCommentListResponse(
    val content: List<FeedCommentResponse>
)

@Entity(tableName = "comment")
data class FeedCommentResponse(
    @PrimaryKey val id: Long,
    val comment: String,
    val createdTime: String,
    val actionDonationHistoryId: Long,
    val mine: Boolean,
    @Embedded(prefix = "user_") val user: CommentUser
)

data class CommentUser(
    val id: Long,
    val name: String,
    val profilePath: String,
    @Embedded(prefix = "department_") val department: Department?
)
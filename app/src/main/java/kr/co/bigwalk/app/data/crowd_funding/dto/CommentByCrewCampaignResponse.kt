package kr.co.bigwalk.app.data.crowd_funding.dto

data class CommentByCrewCampaignResponse (
    val content: List<ContentResponse>,
    val totalElements: Int
)

data class ContentResponse (
    val id: Long,
    val comment: String,
    val createdTime: String,
    val mine: Boolean,
    val userId: Long,
    val userName: String,
    val profilePath: String
)

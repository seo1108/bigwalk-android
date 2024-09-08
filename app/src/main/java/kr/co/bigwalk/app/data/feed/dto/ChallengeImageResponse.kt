package kr.co.bigwalk.app.data.feed.dto

data class ChallengeImageResponse(
    val content: List<ChallengeImageItem>?,
    val totalPages: Int,
    val numberOfElements: Int,
    val totalElements: Int
)

package kr.co.bigwalk.app.data.blame.dto

data class UserBlockListResponse(
    val content: List<UserBlockResponse>
)

data class UserBlockResponse(
    val name: String,
    val profilePath: String,
    val userId: Long
)
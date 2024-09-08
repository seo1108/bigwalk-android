package kr.co.bigwalk.app.data.community

data class MyCommunityListResponse(
    val data : List<MyCommunityResponse>
)

data class MyCommunityResponse(
    val groupId: Long,
    val groupName: String,
    val description: String,
    val thumbnailPath: String
)
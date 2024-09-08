package kr.co.bigwalk.app.data.community

data class DelegateOwnerRequest(
    val userId: Long,
    val role: GroupMemberRole
)

package kr.co.bigwalk.app.data.community

import kr.co.bigwalk.app.util.longValueToCommaString

data class GroupMemberResponse(
    val userId: Long,
    val name: String,
    val profilePath: String,
    val donatedSteps: Long,
    val mine: Boolean,
    val role: GroupMemberRole
) {
    fun getTotalDonatedSteps(): String {
        return if (donatedSteps > 10000) {
            longValueToCommaString(donatedSteps / 10000) + "만" + longValueToCommaString(donatedSteps % 10000)
        } else {
            longValueToCommaString(donatedSteps)
        }
    }
    fun getRoleName(): String {
        return when (role) {
            GroupMemberRole.OWNER -> "마스터"
            GroupMemberRole.MEMBER -> "매니저"
            GroupMemberRole.GUEST -> "체인지메이커"
        }
    }
    fun isOwner(): Boolean {
        return role == GroupMemberRole.OWNER
    }
}
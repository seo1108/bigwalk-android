package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Group
import kr.co.bigwalk.app.data.organization.GroupType
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.UserProvider

data class AutoLoginResponse(
    val id: Long,
    val profilePath: String?,
    val name: String,
    val gender: Gender?,
    val groups: List<Group>,
    val characterId: Int?,
    val blocked: Boolean?
) {
    // legacy, mega, first
    val megaOrganization: Organization
        get() = Organization(
            id = getMegaGroups()?.id,
            name = getMegaGroups()?.name,
            organizationIdType = getMegaGroups()?.organizationIdType
        )
    val megaDepartment: Department?
        get() = getMegaGroups()?.department

    private fun getMegaGroups(): Group? {
        if (groups == null) return null
        groups.lastOrNull { group ->
            group.type == GroupType.MEGA
        }?.let { megaGroup ->
            return megaGroup
        }
        return null
    }

    fun checkIsBlockedUser(): Boolean {
        blocked?.let { return it }
        return false
    }

    fun getGroupTypeForGA(): String {
        return if (groups.map { it.type }.contains(GroupType.MEGA)) "ENTERPRISE_TYPE"
        else "NORMAL_TYPE"
    }

    fun toUserProfileResponse(): UserProfileResponse =
        UserProfileResponse(
            profilePath = profilePath,
            id= id,
            email= "",
            name= name,
            groups= groups,
            campaignNotiAgreement= true,
            storyNotiAgreement= true,
            rankingNotiAgreement= true,
            marketingNotiAgreement= true,
            characterId= characterId,
            userProvider= null,
            blocked= false
        )
}
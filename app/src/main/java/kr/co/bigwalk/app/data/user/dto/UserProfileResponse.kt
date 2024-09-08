package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.user.UserProvider

data class UserProfileResponse(
    var profilePath: String?,
    var id: Long,
    var email: String?,
    var name: String,
    var groups: List<Group>,
    var campaignNotiAgreement: Boolean?,
    var storyNotiAgreement: Boolean?,
    var rankingNotiAgreement: Boolean?,
    var marketingNotiAgreement: Boolean?,
    var characterId: Int?,
    var userProvider: UserProvider?,
    var blocked: Boolean?
) {
    // legacy, mega, first
    val megaGroup: Group?
        get() = getMegaGroups()
    val megaOrganizationEmail: String?
        get() = getMegaGroups()?.value1
    val megaOrganizationEmployeeNumber: String?
        get() = getMegaGroups()?.value2
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
    
    fun getSmallOrMediumGroup(): Group? {
        return groups.lastOrNull { group ->
            group.type == GroupType.SMALL || group.type == GroupType.MEDIUM
        }
    }
    
    fun getWithdrawGoodbyeString(): String {
        return name + "님과의 이별인가요?\n너무 아쉬워요."
    }
    
    fun getCharacterIdString(): String {
        return characterId.toString()
    }
    
    fun checkIsBlockedUser(): Boolean {
        blocked?.let { return it }
        return false
    }

    fun getGroupTypeForGA(): String {
        return if (groups.map { it.type }.contains(GroupType.MEGA)) "ENTERPRISE_TYPE"
        else "NORMAL_TYPE"
    }
}
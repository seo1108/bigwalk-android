package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.organization.OrganizationIdType
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.UserProvider
import java.io.Serializable

data class MyProfileResponse(
    val profilePath: String = "",
    val name: String = "",
    val gender: Gender? = null,
    val email: String? = "",
    val phoneNumber: String? = "",
    private val birthday: String? = "",
    val firstDepthRegion: String? = "",
    val secondDepthRegion: String? = "",
    val userConcerns: List<UserConcernTagResponse>? = null,
    val organizationEmail: String? = null,
    val organizationEmployeeNumber: String = "",
    val organization: OrganizationResponse? = null,
    val department: DepartmentResponse? = null,
    val group: GroupResponse? = null,
    val campaignNotiAgreement: Boolean,
    val marketingNotiAgreement: Boolean,
    val storyNotiAgreement: Boolean,
    val rankingNotiAgreement: Boolean,
    val walkNotiAgreement: Boolean,
    val characterId: Long? = null,
    val userProvider: UserProvider,
    val blocked: Boolean? = null,


    val depth1: DepartmentResponse? = null,
    val depth2: DepartmentResponse? = null,
    val depth3: DepartmentResponse? = null,
    val depth4: DepartmentResponse? = null,
   /* val depth1Title: String? = "",
    val depth2Title: String? = "",
    val depth3Title: String? = "",
    val depth4Title: String? = "",
    val depth1Hint: String? = "",
    val depth2Hint: String? = "",
    val depth3Hint: String? = "",
    val depth4Hint: String? = "",*/
    val searchContent: String? = "",
    val visitDay: String?= "",
    val certNo: String?= "",
    val familyRelations: String?= "",
    val age: String?= "",
    val nickname: String?= "",
    val identification: String?= "",
    val instagramAccount: String?= "",
    val address: String?= "",
    val attendanceNo: String?= "",
    val studentId: String?= "",
    val number: String?= "",
    val input: String?= "",
    val organizationEmployeeName: String?= ""

) : Serializable {
    fun getArea(): String = if (!secondDepthRegion.isNullOrEmpty()) "$firstDepthRegion $secondDepthRegion" else ""
    fun getBirthday(): String {
        val birth = birthday?.replace("-", "").orEmpty()
        return if (birth.length >= 8) birth else ""
    }
}

data class UserConcernTagResponse(
    val id: Long,
    val name: String
) : Serializable

data class DepartmentResponse(
    val id: Long,
    val name: String
) : Serializable

data class OrganizationResponse(
    val id: Long,
    val name: String,
    val organizationIdType: String
) : Serializable

data class GroupResponse(
    val id: Long,
    val organizationId: Long,
    val name: String,
    val expiredDate: String? = null,
    val organizationIdType: OrganizationIdType,
    val departmentId: Long,
    val value1: String? = null,
    val value2: String? = null,
    val value3: String? = null,
    val value4: String? = null,
    val value5: String? = null
) : Serializable

package kr.co.bigwalk.app.data.organization

import java.io.Serializable

data class OrganizationRequirement(
    override val id: Long?,
    override val depth1: String?,
    override val depth2: String?,
    override val depth3: String?,
    override val depth4: String?,
    override val isFamilyRelations: Boolean? = false,
    override val isAge: Boolean? = false,
    override val isNickname: Boolean? = false,
    override val isName: Boolean? = false,
    override val isIdentification: Boolean? = false,
    override val isInstagramAccount: Boolean? = false,
    override val isAddress: Boolean? = false,
    override val isAttendanceNo: Boolean? = false,
    override val isStudentId: Boolean? = false,
    override val isOrganizationEmployeeNumber: Boolean? = false,
    override val isOrganizationEmail: Boolean? = false,
    override val isVisitDay: Boolean? = false,
    override val isInput: Boolean? = false,
    override val isSearchContent: Boolean? = false,
    override val isNumber: Boolean? = false,
    override val isCertNo: Boolean? = false
) : OrganizationRequirementItem, Serializable

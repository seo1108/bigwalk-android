package kr.co.bigwalk.app.data.organization.repository

import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest

object OrganizationLocalDataSource : OrganizationDataSource {

    override fun getOrganizations(getOrganizationsCallback: OrganizationDataSource.GetOrganizationsCallback) {
        val organizations = listOf(
            Organization(1, "빅워크", OrganizationIdType.NONE),
            Organization(2, "저스트리브", OrganizationIdType.EMAIL_EMPLOYEE_NUMBER_BOTH),
            Organization(3, "스파크플러스", OrganizationIdType.EMAIL)
        )
        getOrganizationsCallback.onSuccess(organizations)
    }

    override fun getOrganization(organizationId: Long, getOrganizationCallback: OrganizationDataSource.GetOrganizationCallback) {
    }

    override fun getDepartments(organizationId: Long, getDepartmentsCallback: OrganizationDataSource.GetDepartmentsCallback) {
        val departments = listOf(
            Department(1, "기획"),
            Department(2, "경영"),
            Department(3, "개발"),
            Department(4, "디자인")
        )
        getDepartmentsCallback.onSuccess(departments)
    }

    override fun getSubDepartments(organizationId: Long, depth: Long, parentId: Long, getSubDepartmentsCallback: OrganizationDataSource.GetSubDepartmentsCallback) {
        val departments = listOf(
            Department(1, "기획"),
            Department(2, "경영"),
            Department(3, "개발"),
            Department(4, "디자인")
        )
        getSubDepartmentsCallback.onSuccess(departments)
    }

    override fun getSearchKeyword(organizationId: Long, getSearchKeywordCallback: OrganizationDataSource.GetSearchKeywordCallback) {
        val contents = listOf(
            Content("검색어")
        )
        getSearchKeywordCallback.onSuccess(contents)
    }

    override fun verifyAuthNum(organizationId: Long, request: CertNoRequest, verifyAuthNumCallback: OrganizationDataSource.VerifyAuthNumCallback) {
        val isValid = true
        verifyAuthNumCallback.onSuccess(isValid)
    }

    override fun saveOrganizationInfo(request: OrganizationRequest, saveOrganizationInfoCallback: OrganizationDataSource.SaveOrganizationInfoCallback) {
        saveOrganizationInfoCallback.onSuccess()
    }

    override fun getOrganizationRequirement(organizationId: Long, getOrganizationRequirementCallback: OrganizationDataSource.GetOrganizationRequirementCallback) {
        val organizationrequirement =
            OrganizationRequirement(1, "그룹", "계열", "그룹", "단체", true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)

        getOrganizationRequirementCallback.onSuccess(organizationrequirement)
    }

    override fun getSpaceOrganization(organizationId: Long, getOrganizationCallback: OrganizationDataSource.GetSpaceOrganizationCallback) {
    }

    override fun addProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        addProfileByOrganizationCallback: OrganizationDataSource.AddProfileByOrganizationCallback
    ) {
    }

    override fun modifyProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        modifyProfileByOrganizationCallback: OrganizationDataSource.ModifyProfileByOrganizationCallback
    ) {
    }

}
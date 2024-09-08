package kr.co.bigwalk.app.data.organization.repository

import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.space.SpaceOrganizationResponse
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest

interface  OrganizationDataSource {

    interface GetOrganizationsCallback {
        fun onSuccess(selectableOrganizations: List<Organization>?)
        fun onFailed(reason: String)
    }

    fun getOrganizations(getOrganizationsCallback: GetOrganizationsCallback)

    interface GetOrganizationRequirementCallback {
        fun onSuccess(selectableOrganizationsRequirement: OrganizationRequirement?)
        fun onFailed(reason: String)
    }

    fun getOrganizationRequirement(organizationId: Long, getOrganizationRequirementCallback: GetOrganizationRequirementCallback)

    interface GetOrganizationCallback {
        fun onSuccess(selectableOrganizations: Organization?)
        fun onFailed(reason: String)
    }

    fun getOrganization(organizationId: Long, getOrganizationCallback: GetOrganizationCallback)

    interface GetDepartmentsCallback {
        fun onSuccess(selectableDepartments: List<Department>?)
        fun onFailed(reason: String)
    }

    fun getDepartments(organizationId: Long, getDepartmentsCallback: GetDepartmentsCallback)

    interface GetSubDepartmentsCallback {
        fun onSuccess(selectableSubDepartments: List<Department>?)
        fun onFailed(reason: String)
    }

    fun getSubDepartments(organizationId: Long, depth: Long, parentId: Long, getSubDepartmentsCallback: GetSubDepartmentsCallback)

    interface GetSearchKeywordCallback {
        fun onSuccess(selectableContents: List<Content>?)
        fun onFailed(reason: String)
    }

    fun getSearchKeyword(organizationId: Long, getSearchKeywordCallback: GetSearchKeywordCallback)

    interface GetSpaceOrganizationCallback {
        fun onSuccess(spaceOrganizationResponse: SpaceOrganizationResponse?)
        fun onFailed(reason: String)
    }

    fun verifyAuthNum(organizationId: Long, request: CertNoRequest, verifyAuthNumCallback: VerifyAuthNumCallback)

    interface VerifyAuthNumCallback {
        fun onSuccess(isValid: Boolean)
        fun onFailed(reason: String)
    }

    fun saveOrganizationInfo(request: OrganizationRequest, saveOrganizationInfoCallback: SaveOrganizationInfoCallback)

    interface SaveOrganizationInfoCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun getSpaceOrganization(organizationId: Long, getOrganizationCallback: GetSpaceOrganizationCallback)

    interface AddProfileByOrganizationCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun addProfileByOrganization(spaceUserRequest: SpaceUserRequest, addProfileByOrganizationCallback: AddProfileByOrganizationCallback)

    interface ModifyProfileByOrganizationCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun modifyProfileByOrganization(spaceUserRequest: SpaceUserRequest, modifyProfileByOrganizationCallback: ModifyProfileByOrganizationCallback)
}
package kr.co.bigwalk.app.data.organization.repository

import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest

object OrganizationRepository : OrganizationDataSource {

    private val organizationDataSource: OrganizationDataSource

    init {
        @Suppress("ConstantConditionIf")
        organizationDataSource = if (BuildConfig.FLAVOR == "local") OrganizationLocalDataSource else OrganizationRemoteDataSource
    }

    override fun getOrganizations(getOrganizationsCallback: OrganizationDataSource.GetOrganizationsCallback) {
        organizationDataSource.getOrganizations(getOrganizationsCallback)
    }

    override fun getOrganization(organizationId: Long, getOrganizationCallback: OrganizationDataSource.GetOrganizationCallback) {
        organizationDataSource.getOrganization(organizationId, getOrganizationCallback)
    }

    override fun getOrganizationRequirement(organizationId: Long, getOrganizationRequirementCallback: OrganizationDataSource.GetOrganizationRequirementCallback) {
        organizationDataSource.getOrganizationRequirement(organizationId, getOrganizationRequirementCallback)
    }

    override fun getDepartments(organizationId: Long, getDepartmentsCallback: OrganizationDataSource.GetDepartmentsCallback) {
        organizationDataSource.getDepartments(organizationId, getDepartmentsCallback)
    }

    override fun getSubDepartments(organizationId: Long, depth: Long, parentId: Long, getSubDepartmentsCallback: OrganizationDataSource.GetSubDepartmentsCallback) {
        organizationDataSource.getSubDepartments(organizationId, depth, parentId, getSubDepartmentsCallback)
    }

    override fun getSearchKeyword(organizationId: Long, getSearchKeywordCallback: OrganizationDataSource.GetSearchKeywordCallback) {
        organizationDataSource.getSearchKeyword(organizationId, getSearchKeywordCallback)
    }

    override fun verifyAuthNum(organizationId: Long, request: CertNoRequest, verifyAuthNumCallback: OrganizationDataSource.VerifyAuthNumCallback) {
        organizationDataSource.verifyAuthNum(organizationId, request, verifyAuthNumCallback)
    }

    override fun saveOrganizationInfo(request: OrganizationRequest, saveOrganizationInfoCallback: OrganizationDataSource.SaveOrganizationInfoCallback) {
        organizationDataSource.saveOrganizationInfo(request, saveOrganizationInfoCallback)
    }

    override fun getSpaceOrganization(
        organizationId: Long,
        getOrganizationCallback: OrganizationDataSource.GetSpaceOrganizationCallback
    ) {
        organizationDataSource.getSpaceOrganization(organizationId, getOrganizationCallback)
    }

    override fun addProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        addProfileByOrganizationCallback: OrganizationDataSource.AddProfileByOrganizationCallback
    ) {
        organizationDataSource.addProfileByOrganization(spaceUserRequest, addProfileByOrganizationCallback)
    }

    override fun modifyProfileByOrganization(
        spaceUserRequest: SpaceUserRequest,
        modifyProfileByOrganizationCallback: OrganizationDataSource.ModifyProfileByOrganizationCallback
    ) {
        organizationDataSource.modifyProfileByOrganization(spaceUserRequest, modifyProfileByOrganizationCallback)
    }
}
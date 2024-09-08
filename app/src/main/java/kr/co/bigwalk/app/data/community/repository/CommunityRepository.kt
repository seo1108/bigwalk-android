package kr.co.bigwalk.app.data.community.repository

import kr.co.bigwalk.app.data.ResultCodeState
import kr.co.bigwalk.app.data.community.*
import kr.co.bigwalk.app.data.community.dto.MyRoleFromGroupResponse
import kr.co.bigwalk.app.data.community.dto.create.*

object CommunityRepository {
    fun getGroupMemberList(
        groupId: Long,
        successCallback: (List<GroupMemberResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getGroupMemberList(groupId, successCallback, failCallback)
    }

    fun joinGroup(
        groupId: Long,
        request: GroupJoinRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.joinGroup(groupId, request, successCallback, failCallback)
    }

    fun joinGroupDry(
        groupId: Long,
        successCallback: (GroupSummaryInfoResponse?) -> Unit,
        failCallback: (ResultCodeState) -> Unit
    ) {
        return CommunityRemoteDataSource.joinGroupDry(groupId, successCallback, failCallback)
    }

    fun delegateGroupOwner(
        groupId: Long,
        request: DelegateOwnerRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.delegateGroupOwner(groupId, request, successCallback, failCallback)
    }

    fun kickGroupMember(
        groupId: Long,
        userId: Long,
        successCallback: (List<GroupMemberResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.kickGroupMember(groupId, userId, successCallback, failCallback)
    }

    fun leaveGroup(
        groupId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.leaveGroup(groupId, successCallback, failCallback)
    }

    fun getGroupInviteKey(
        groupId: Long,
        successCallback: (GroupInviteKeyResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getGroupInviteKey(groupId, successCallback, failCallback)
    }

    fun getGroupDetail(
        groupId: Long,
        successCallback: (GroupDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getGroupDetail(groupId, successCallback, failCallback)
    }

    fun setGroupGoal(
        groupId: Long,
        request: GroupGoalRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.setGroupGoal(groupId, request, successCallback, failCallback)
    }

    fun getGroupShareContents(
        groupId: Long,
        successCallback: (GroupShareResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getGroupShareContents(groupId, successCallback, failCallback)
    }

    fun getMyGroupList(
        successCallback: (MyCommunityListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getMyGroupList(successCallback, failCallback)
    }

    fun getModifyCrewInfo(
        groupId: Long,
        successCallback: (ModifyCrewInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getModifyCrewInfo(groupId, successCallback, failCallback)
    }

    fun modifyCrew(
        groupId: Long,
        request: ModifyCrewRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.modifyCrew(groupId, request, successCallback, failCallback)
    }

    fun getMyRoleFromGroup(
        groupId: Long,
        successCallback: (MyRoleFromGroupResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getMyRoleFromGroup(groupId, successCallback, failCallback)
    }

    fun getCrewRequestList(
        groupId: Long,
        page: Int,
        size: Int,
        successCallback: (CrewRequestListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getCrewRequestList(groupId, page, size, successCallback, failCallback)
    }

    fun approveCrewMember(
        groupId: Long,
        requestId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.approveCrewMember(groupId, requestId, successCallback, failCallback)
    }

    fun rejectCrewMember(
        groupId: Long,
        requestId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.rejectCrewMember(groupId, requestId, successCallback, failCallback)
    }

    fun getCrewAddress(
        successCallback: (CrewAddressResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getCrewAddress(successCallback, failCallback)
    }

    fun getCrewConcern(
        groupId: Long?,
        successCallback: (List<CrewConcernResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.getCrewConcern(groupId, successCallback, failCallback)
    }

    fun duplicateCheckForCrewTitle(
        crewTitle: String,
        successCallback: (DuplicateCheckForCrewTitleResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.duplicateCheckForCrewTitle(crewTitle, successCallback, failCallback)
    }

    fun registerCrew(
        request: RegisterCrewRequest,
        successCallback: (Pair<RegisterCrewResponse?, String?>) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CommunityRemoteDataSource.registerCrew(request, successCallback, failCallback)

    }
}
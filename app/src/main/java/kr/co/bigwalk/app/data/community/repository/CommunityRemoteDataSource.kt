package kr.co.bigwalk.app.data.community.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.CodeType
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.ResultCodeState
import kr.co.bigwalk.app.data.community.*
import kr.co.bigwalk.app.data.community.dto.MyRoleFromGroupResponse
import kr.co.bigwalk.app.data.community.dto.create.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CommunityRemoteDataSource {
    fun getGroupMemberList(
        groupId: Long,
        successCallback: (List<GroupMemberResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getGroupMemberList(groupId)
            .enqueue(object : Callback<List<GroupMemberResponse>> {
                override fun onResponse(call: Call<List<GroupMemberResponse>>, response: Response<List<GroupMemberResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<List<GroupMemberResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun joinGroupDry(
        groupId: Long,
        successCallback: (GroupSummaryInfoResponse?) -> Unit,
        failCallback: (ResultCodeState) -> Unit
    ) {
        return RemoteApiManager.getService().joinGroupDry(groupId)
            .enqueue(object : Callback<GroupSummaryInfoResponse> {
                override fun onResponse(call: Call<GroupSummaryInfoResponse>, response: Response<GroupSummaryInfoResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> {
                            response.errorBody() ?: return
                            val errorResponse = RemoteApiManager.getErrorResponse(response.errorBody()!!)
                            failCallback(ResultCodeState(false, errorResponse.code, errorResponse.message))
                        }
                    }
                }

                override fun onFailure(call: Call<GroupSummaryInfoResponse>, t: Throwable) {
                    failCallback(ResultCodeState(false, CodeType.ETC_ERROR_CODE.code, t.localizedMessage))
                }
            })
    }

    fun joinGroup(
        groupId: Long,
        request: GroupJoinRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().joinGroup(groupId, request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun delegateGroupOwner(
        groupId: Long,
        request: DelegateOwnerRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().delegateGroupOwner(groupId, request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun kickGroupMember(
        groupId: Long,
        userId: Long,
        successCallback: (List<GroupMemberResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().kickGroupMember(groupId, userId)
            .enqueue(object : Callback<List<GroupMemberResponse>> {
                override fun onResponse(call: Call<List<GroupMemberResponse>>, response: Response<List<GroupMemberResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<List<GroupMemberResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun leaveGroup(
        groupId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().leaveGroup(groupId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getGroupInviteKey(
        groupId: Long,
        successCallback: (GroupInviteKeyResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getGroupInviteKey(groupId)
            .enqueue(object : Callback<GroupInviteKeyResponse> {
                override fun onResponse(call: Call<GroupInviteKeyResponse>, response: Response<GroupInviteKeyResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<GroupInviteKeyResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getGroupDetail(
        groupId: Long,
        successCallback: (GroupDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getGroupDetail(groupId)
            .enqueue(object : Callback<GroupDetailResponse> {
                override fun onResponse(call: Call<GroupDetailResponse>, response: Response<GroupDetailResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<GroupDetailResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun setGroupGoal(
        groupId: Long,
        request: GroupGoalRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().setGroupGoal(groupId, request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        200 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getGroupShareContents(
        groupId: Long,
        successCallback: (GroupShareResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getGroupShareContents(groupId)
            .enqueue(object : Callback<GroupShareResponse> {
                override fun onResponse(call: Call<GroupShareResponse>, response: Response<GroupShareResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<GroupShareResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getMyGroupList(
        successCallback: (MyCommunityListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyCrewList()
            .enqueue(object : Callback<MyCommunityListResponse> {
                override fun onResponse(call: Call<MyCommunityListResponse>, response: Response<MyCommunityListResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MyCommunityListResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getModifyCrewInfo(
        groupId: Long,
        successCallback: (ModifyCrewInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getModifyCrewInfo(groupId)
            .enqueue(object : Callback<ModifyCrewInfoResponse> {
                override fun onResponse(call: Call<ModifyCrewInfoResponse>, response: Response<ModifyCrewInfoResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<ModifyCrewInfoResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun modifyCrew(
        groupId: Long,
        request: ModifyCrewRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().modifyCrew(groupId, request.toMultipartBody())
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        204 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getMyRoleFromGroup(
        groupId: Long,
        successCallback: (MyRoleFromGroupResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyRoleFromGroup(groupId)
            .enqueue(object : Callback<MyRoleFromGroupResponse> {
                override fun onResponse(call: Call<MyRoleFromGroupResponse>, response: Response<MyRoleFromGroupResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MyRoleFromGroupResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewRequestList(
        groupId: Long,
        page: Int,
        size: Int,
        successCallback: (CrewRequestListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewRequestList(groupId, page, size)
            .enqueue(object : Callback<CrewRequestListResponse> {
                override fun onResponse(call: Call<CrewRequestListResponse>, response: Response<CrewRequestListResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CrewRequestListResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun approveCrewMember(
        groupId: Long,
        requestId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().approveCrewMember(groupId, requestId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        201 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun rejectCrewMember(
        groupId: Long,
        requestId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().rejectCrewMember(groupId, requestId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        204 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewAddress(
        successCallback: (CrewAddressResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewAddress()
            .enqueue(object : Callback<BaseResponse<CrewAddressResponse>> {
                override fun onResponse(call: Call<BaseResponse<CrewAddressResponse>>, response: Response<BaseResponse<CrewAddressResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body()?.data)
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CrewAddressResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewConcern(
        groupId: Long?,
        successCallback: (List<CrewConcernResponse>?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewConcern(groupId)
            .enqueue(object : Callback<BaseResponse<List<CrewConcernResponse>>> {
                override fun onResponse(call: Call<BaseResponse<List<CrewConcernResponse>>>, response: Response<BaseResponse<List<CrewConcernResponse>>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body()?.data)
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CrewConcernResponse>>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun duplicateCheckForCrewTitle(
        crewTitle: String,
        successCallback: (DuplicateCheckForCrewTitleResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().duplicateCheckForCrewTitle(crewTitle)
            .enqueue(object : Callback<BaseResponse<DuplicateCheckForCrewTitleResponse>> {
                override fun onResponse(call: Call<BaseResponse<DuplicateCheckForCrewTitleResponse>>, response: Response<BaseResponse<DuplicateCheckForCrewTitleResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body()?.data)
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<DuplicateCheckForCrewTitleResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun registerCrew(
        request: RegisterCrewRequest,
        successCallback: (Pair<RegisterCrewResponse?, String?>) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().registerCrew(request.toMultipartBody())
            .enqueue(object : Callback<BaseResponse<RegisterCrewResponse>> {
                override fun onResponse(call: Call<BaseResponse<RegisterCrewResponse>>, response: Response<BaseResponse<RegisterCrewResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(Pair(response.body()?.data, response.body()?.message))
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<RegisterCrewResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }
}
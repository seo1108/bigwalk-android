package kr.co.bigwalk.app.community.funding.contest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.data.funding.dto.ContestDetailResponse
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog

class ContestDetailViewModel(private val contestId: Long): ViewModel() {

    private val _contestDetail = MutableLiveData<ContestDetailResponse>()
    val contestDetail: LiveData<ContestDetailResponse> get() = _contestDetail

    private val _myRole = MutableLiveData<GroupMemberRole>()
    val myRole: LiveData<GroupMemberRole> get() = _myRole

    init {
        getContestDetail()
    }

    fun getMyRoleFromGroup(groupId: Long) {
        CommunityRepository.getMyRoleFromGroup(
            groupId = groupId,
            successCallback = { response ->
                if (response != null) {
                    _myRole.value = response.myRole
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun getContestDetail() {
        FundingRepository.getContestDetail(
            competitionId = contestId,
            successCallback = { response ->
                _contestDetail.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }

}
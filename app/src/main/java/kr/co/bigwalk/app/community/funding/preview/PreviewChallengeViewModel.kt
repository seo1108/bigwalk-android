package kr.co.bigwalk.app.community.funding.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.data.funding.dto.ChallengeOfCrewCampaignResponse
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event

class PreviewChallengeViewModel(private val crewCampaignId: Long) : ViewModel() {

    private val _challengeOfCrewCampaign = MutableLiveData<ChallengeOfCrewCampaignResponse>()
    val challengeOfCrewCampaign: LiveData<ChallengeOfCrewCampaignResponse> get() = _challengeOfCrewCampaign

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _deleteEvent = MutableLiveData<Event<Unit>>()
    val deleteEvent: LiveData<Event<Unit>> get() = _deleteEvent

    private val _myRole = MutableLiveData<GroupMemberRole>()
    val myRole: LiveData<GroupMemberRole> get() = _myRole

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

    fun requestChallengeOfCrewCampaign() {
        FundingRepository.getPreviewChallengeOfSupporters(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _challengeOfCrewCampaign.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }

    fun deleteCrewCampaign() {
        FundingRepository.deleteSupportersCampaign(
            supportersCampaignId = crewCampaignId,
            successCallback = {
                _toastMessage.value = Event("삭제가 완료되었습니다")
                _deleteEvent.value = Event(Unit)
            },
            failCallback = {
                DebugLog.d(it)
                _toastMessage.value = Event(it)
            }
        )
    }
}
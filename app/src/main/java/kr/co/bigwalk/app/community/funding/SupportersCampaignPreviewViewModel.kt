package kr.co.bigwalk.app.community.funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.data.funding.CampaignRegisterStatus
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignResponse
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event

class SupportersCampaignPreviewViewModel(private val crewCampaignId: Long) : ViewModel() {

    private val _crewCampaign = MutableLiveData<CrewCampaignResponse>()
    val crewCampaign: LiveData<CrewCampaignResponse> get() = _crewCampaign

    private val _hasActionMission = MutableLiveData<Boolean>()
    val hasActionMission: LiveData<Boolean> get() = _hasActionMission

    private val _progressData = MutableLiveData<JudgeProgressItem>()
    val progressData: LiveData<JudgeProgressItem> get() = _progressData

    private val _judgeSuccess = MutableLiveData<Event<Unit>>()
    val judgeSuccess: LiveData<Event<Unit>> get() = _judgeSuccess

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _deleteEvent = MutableLiveData<Event<Unit>>()
    val deleteEvent: LiveData<Event<Unit>> get() = _deleteEvent

    private val _myRole = MutableLiveData<GroupMemberRole>()
    val myRole: LiveData<GroupMemberRole> get() = _myRole

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> get() = _errorEvent

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

    fun requestCrewCampaign() {
        FundingRepository.getCrewCampaign(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _crewCampaign.value = response
                if (response != null) {
                    _hasActionMission.value = response.hasActionMission ?: false
                    setProgressData(response.crewCampaignRegisterStatus)
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
                _errorEvent.value = Event(it)
            })
    }

    private fun setProgressData(crewCampaignRegisterStatus: CampaignRegisterStatus?) {
        when (crewCampaignRegisterStatus) {
            CampaignRegisterStatus.TEMPORARY_SAVE -> {
                _progressData.value = JudgeProgressItem(
                    lottieRes = R.raw.aos_neon_1,
                    btnEnable = true,
                    btnTitle = BigwalkApplication.context?.getString(R.string.application_for_examination).orEmpty(),
                    subTitle = BigwalkApplication.context?.getString(R.string.application_for_examination_explain).orEmpty()
                )
            }
            CampaignRegisterStatus.SCREENING -> {
                _progressData.value = JudgeProgressItem(
                    lottieRes = R.raw.aos_neon_2,
                    btnEnable = false,
                    btnTitle = BigwalkApplication.context?.getString(R.string.application_for_examination_in_progress).orEmpty(),
                    subTitle = BigwalkApplication.context?.getString(R.string.application_for_examination_in_progress_explain).orEmpty()
                )
            }
            CampaignRegisterStatus.FUNDING -> {
                _progressData.value = JudgeProgressItem(
                    lottieRes = R.raw.aos_funding_neon,
                    btnEnable = false,
                    btnTitle = BigwalkApplication.context?.getString(R.string.application_for_examination).orEmpty(),
                    subTitle = BigwalkApplication.context?.getString(R.string.application_for_examination_end_explain).orEmpty()
                )
            }
            else -> {
                _progressData.value = JudgeProgressItem(
                    lottieRes = R.raw.aos_neon_1,
                    btnEnable = false,
                    btnTitle = BigwalkApplication.context?.getString(R.string.application_for_examination).orEmpty(),
                    subTitle = BigwalkApplication.context?.getString(R.string.application_for_examination_explain).orEmpty()
                )
            }
        }
    }

    fun judgeCrewCampaign(groupId: Long) {
        FundingRepository.judgeCrewCampaign(
            crewCampaignId = crewCampaignId,
            groupId = groupId,
            successCallback = {
                _judgeSuccess.value = Event(Unit)
                requestCrewCampaign()
            }, failCallback = {
                _toastMessage.value = Event(it)
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

    fun fetchLastModifier() {
        FundingRepository.getLastModifier(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                if (response != null) {
                    _toastMessage.value =
                        Event("마지막 수정자: ${response.lastModifiedBy}\n마지막 수정 날짜: ${response.getLastModifiedDateToString()}")
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    data class JudgeProgressItem(
        val lottieRes: Int,
        val btnEnable: Boolean,
        val btnTitle: String,
        val subTitle: String
    )
}
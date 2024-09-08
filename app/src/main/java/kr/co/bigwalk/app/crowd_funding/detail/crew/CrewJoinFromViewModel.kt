package kr.co.bigwalk.app.crowd_funding.detail.crew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.funding.dto.ApplyForCrewCampaignRequest
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event

class CrewJoinFromViewModel(private val crewCampaignId: Long) : ViewModel() {
    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _successEvent = MutableLiveData<Event<Unit>>()
    val successEvent: LiveData<Event<Unit>> get() = _successEvent

    fun applyForCrewCampaign(answer: String) {
        if (answer.length < 2) {
            _toastMessage.value = Event("문항 작성을 완료해 주세요.")
            return
        }
        FundingRepository.applyForCrewCampaign(
            crewCampaignId = crewCampaignId,
            request = ApplyForCrewCampaignRequest(
                answer = answer
            ),
            successCallback = {
                _toastMessage.value = Event("크루 가입 신청이 완료되었습니다.")
                _successEvent.value = Event(Unit)
            }, failCallback = {
                DebugLog.d(it)
                _toastMessage.value = Event(it)
            }
        )
    }
}
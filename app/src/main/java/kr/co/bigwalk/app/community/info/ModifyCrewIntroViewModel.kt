package kr.co.bigwalk.app.community.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.ModifyCrewInfoResponse
import kr.co.bigwalk.app.data.community.ModifyCrewRequest
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog

class ModifyCrewIntroViewModel(private val groupId: Long) : ViewModel() {
    private val _modifyCrewInfo = MutableLiveData<ModifyCrewInfoResponse>()
    val modifyCrewInfo: LiveData<ModifyCrewInfoResponse> get() = _modifyCrewInfo
    
    private val _successMessage = MutableLiveData<Int>()
    val successMessage: LiveData<Int> get() = _successMessage
    
    private val _failureMessage = MutableLiveData<String>()
    val failureMessage: LiveData<String> get() = _failureMessage

    init {
        getModifyCrewInfo()
    }

    private fun getModifyCrewInfo() {
        CommunityRepository.getModifyCrewInfo(
            groupId = groupId,
            successCallback = { response ->
                _modifyCrewInfo.value = response
                DebugLog.d(response.toString())
            },
            failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun modifyCrew(request: ModifyCrewRequest) {
        CommunityRepository.modifyCrew(
            groupId = groupId,
            request = request,
            successCallback = {
                _successMessage.value = R.string.modify_crew_intro_success_msg
            },
            failCallback = {
                _failureMessage.value = it
                DebugLog.d(it)
            }
        )
    }
}


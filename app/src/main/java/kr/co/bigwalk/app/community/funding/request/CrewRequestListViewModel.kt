package kr.co.bigwalk.app.community.funding.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gun0912.tedimagepicker.util.ToastUtil
import kr.co.bigwalk.app.data.community.CrewRequestListResponse
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog

class CrewRequestListViewModel(private val groupId: Long) : ViewModel() {

    val crewRequestList = MutableLiveData<CrewRequestListResponse>()
    val crewRequestJoinCount = MutableLiveData<Int>()

    init {
        getRequestList()
    }

    private fun getRequestList() {
        CommunityRepository.getCrewRequestList(
            groupId,
            0,
            100,
            successCallback = {
                if (it != null) {
                    crewRequestList.value = it
                    crewRequestJoinCount.value = it.content.size
                }
                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun approve(groupId: Long, requestId: Long) {
        CommunityRepository.approveCrewMember(
            groupId = groupId,
            requestId = requestId,
            successCallback = {
                ToastUtil.showToast("승인하였습니다.")
                getRequestList()
            },
            failCallback = {
                ToastUtil.showToast(it)
            }
        )
    }

    fun reject(groupId: Long, requestId: Long) {
        CommunityRepository.rejectCrewMember(
            groupId = groupId,
            requestId = requestId,
            successCallback = {
                ToastUtil.showToast("거절하였습니다.")
                getRequestList()
            },
            failCallback = {
                ToastUtil.showToast(it)
            }
        )
    }
}
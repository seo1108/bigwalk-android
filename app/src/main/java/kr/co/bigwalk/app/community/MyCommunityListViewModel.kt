package kr.co.bigwalk.app.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.ResultDataState
import kr.co.bigwalk.app.data.community.AllGroupReportResponse
import kr.co.bigwalk.app.data.community.GroupJoinRequest
import kr.co.bigwalk.app.data.community.MyCommunityListResponse
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCommunityListViewModel : ViewModel() {
    private val _communityReport = MutableLiveData<AllGroupReportResponse>()
    val communityReport: LiveData<AllGroupReportResponse> get() = _communityReport
    private val _myCrewList = MutableLiveData<MyCommunityListResponse>()
    val myCrewList: LiveData<MyCommunityListResponse> get() = _myCrewList

    val joinResult = MutableLiveData<ResultDataState>()

    init {
        getCommunityReport()
    }

    private fun getCommunityReport() {
        RemoteApiManager.getService()
            .getAllGroupReport()
            .enqueue(object : Callback<AllGroupReportResponse> {
                override fun onResponse(call: Call<AllGroupReportResponse>, response: Response<AllGroupReportResponse>) {
                    _communityReport.value = response.body()
                    DebugLog.d(response.body().toString())
                }

                override fun onFailure(call: Call<AllGroupReportResponse>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                }

            })
    }

    fun getCrewList() {
        CommunityRepository.getMyGroupList(
            successCallback = { crewList ->
                _myCrewList.value = crewList
                DebugLog.d(crewList.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }

    fun joinGroup(groupId: Long, key: String?) {
        key?.let { key ->
            CommunityRepository.joinGroup(
                groupId = groupId,
                request = GroupJoinRequest(PreferenceManager.getUserId(), key),
                successCallback = {
                    joinResult.postValue(ResultDataState(result = true, data = groupId))
                },
                failCallback = {
                    joinResult.postValue(ResultDataState(false, it))
                }
            )
        } ?: run {
            joinResult.postValue(ResultDataState(false, BigwalkApplication.context?.getString(R.string.invalid_invite_link)))
        }
    }

}
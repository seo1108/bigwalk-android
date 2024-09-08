package kr.co.bigwalk.app.my_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.organization.OrganizationRequirement
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.getCompleteWord
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageViewModel2 : ViewModel() {
    private val _organizationRequirement = MutableLiveData<OrganizationRequirement>()
    val organizationRequirement: LiveData<OrganizationRequirement> get() = _organizationRequirement

    private val _depth1Title = MutableLiveData<String>()
    val depth1Title: LiveData<String> get() = _depth1Title

    private val _moveEvent = MutableLiveData<Event<MyProfileResponse>>()
    val moveEvent: LiveData<Event<MyProfileResponse>> get() = _moveEvent

    private val _myProfile = MutableLiveData<MyProfileResponse>()
    val myProfile: LiveData<MyProfileResponse> get() = _myProfile

    fun fetchMyProfile() {
        RemoteApiManager.getUserApi().getMyProfile()
            .enqueue(object : Callback<BaseResponse<MyProfileResponse>> {
                override fun onResponse(call: Call<BaseResponse<MyProfileResponse>>, response: Response<BaseResponse<MyProfileResponse>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let { it ->
                                _myProfile.value = it
                                it.organization!!.id?.let { it1 ->
                                    requestOrganizationRequirements(it1)
                                }
                            }
                        }
                        Result.FAIL -> {
                            response.body()?.message?.let {
                                showToast(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<MyProfileResponse>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
    }

    fun moveToModifyMyPage() {
        myProfile.value?.let {
            _moveEvent.value = Event(it)
        }
    }

    private fun requestOrganizationRequirements(organizationId: Long) {
        OrganizationRepository.getOrganizationRequirement(organizationId, object: OrganizationDataSource.GetOrganizationRequirementCallback {
            override fun onSuccess(selectableOrganizationsRequirement: OrganizationRequirement?) {
                if (selectableOrganizationsRequirement != null) {
                    _organizationRequirement.value = selectableOrganizationsRequirement!!
                    if (!selectableOrganizationsRequirement.depth1.isNullOrBlank()) {
                        _depth1Title.value = selectableOrganizationsRequirement!!.depth1+"명"

                    }
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }

}
package kr.co.bigwalk.app.space

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.organization.space.SpaceOrganizationResponse
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import java.text.SimpleDateFormat
import java.util.*

class SpaceGroupMemberFormViewModel : ViewModel() {
    private val _spaceOrganization = MutableLiveData<SpaceOrganizationResponse>()
    val spaceOrganization: LiveData<SpaceOrganizationResponse> get() = _spaceOrganization
    private val _successEvent = MutableLiveData<Unit>()
    val successEvent: LiveData<Unit> get() = _successEvent
    val expiredDate = MutableLiveData<String>()
    val expiredGroup = MutableLiveData<String>()
    var hasAgreeWithEnterprisePrivacy = ObservableBoolean(false)

    fun fetchSpaceGroup(groupId: Long) {
        OrganizationRepository
            .getSpaceOrganization(groupId, object : OrganizationDataSource.GetSpaceOrganizationCallback {
                override fun onSuccess(spaceOrganizationResponse: SpaceOrganizationResponse?) {
                    spaceOrganizationResponse?.let { _spaceOrganization.value = it }
                    val expiredDateResponse = spaceOrganizationResponse?.expiredDate
                    val groupName = spaceOrganizationResponse?.name

                    if (expiredDateResponse != null) {
                        val currentTime = SimpleDateFormat("yyyy.MM.dd").format(System.currentTimeMillis())
                        val expiredTime = SimpleDateFormat("yyyy.MM.dd").format(expiredDateResponse)

                        when {
                            expiredTime == currentTime -> {
                                val pattern = "hh:mm"
                                expiredDate.value = setExpiredMessage(expiredDateResponse, groupName.orEmpty(), pattern)
                            }
                            expiredTime > currentTime -> {
                                val pattern = "yyyy/MM/dd"
                                expiredDate.value = setExpiredMessage(expiredDateResponse, groupName.orEmpty(), pattern)

                            }
                            else -> {
                                expiredGroup.value = groupName.orEmpty()
                            }
                        }
                    } else {
                        expiredDate.value = ""
                    }

                    DebugLog.d(spaceOrganizationResponse.toString())
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                }

            })
    }

    fun setExpiredMessage(expiredDateResponse: Date, groupName: String, pattern: String): String {
        return BigwalkApplication.context?.resources?.let {
            String.format(
                it.getString(R.string.change_group_expired_message),
                groupName,
                SimpleDateFormat(pattern).format(expiredDateResponse)
            )
        }.orEmpty()
    }

    fun agreeWithService() {
        hasAgreeWithEnterprisePrivacy.set(!hasAgreeWithEnterprisePrivacy.get())
    }

    fun changeOrganizationAccount(content1: String, content2: String, content3: String, content4: String, content5: String) {
        if (!hasAgreeWithEnterprisePrivacy.get()) {
            showToast("개인정보처리방침 및 동의에 체크해주세요!")
            return
        }
        spaceOrganization.value?.let {
            if (it.options.size > 0 && content1.isEmpty() && it.options[0].isNecessary) {
                showToast(it.options[0].placeholder)
                return
            }
            if (it.options.size > 1 && content2.isEmpty() && it.options[1].isNecessary) {
                showToast(it.options[1].placeholder)
                return
            }
            if (it.options.size > 2 && content3.isEmpty() && it.options[2].isNecessary) {
                showToast(it.options[2].placeholder)
                return
            }
            if (it.options.size > 3 && content4.isEmpty() && it.options[3].isNecessary) {
                showToast(it.options[3].placeholder)
                return
            }
            if (it.options.size > 4 && content5.isEmpty() && it.options[4].isNecessary) {
                showToast(it.options[4].placeholder)
                return
            }
        }
        val request = SpaceUserRequest(
            organizationId = spaceOrganization.value?.id ?: -1,
            departmentId = 0
        )
        request.toSpaceUserRequest(content1, content2, content3, content4, content5)
        registerSpaceGroup(request)
    }

    private fun registerSpaceGroup(request: SpaceUserRequest) {
        OrganizationRepository
            .addProfileByOrganization(request, object : OrganizationDataSource.AddProfileByOrganizationCallback {
                override fun onSuccess() {
                    _successEvent.value = Unit
                    PreferenceManager.apply {
                        saveOrganization(spaceOrganization.value?.id ?: -1)
                        saveOrganizationName(spaceOrganization.value?.name)
                    }
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                }

            })
    }
}
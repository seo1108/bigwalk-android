package kr.co.bigwalk.app.dialog

import android.app.Activity
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class WelcomeViewModel(
    private val activity: Activity,
    private val dialog: WelcomeDialog
    ): BaseObservable() {
    val profileImage : ObservableField<String> = ObservableField()
    val name: ObservableField<String> = ObservableField()
    val startButtonText: ObservableField<String> = ObservableField(
        if (PreferenceManager.getWelcomeMissionStatus()!="none") {
            activity.resources.getString(R.string.welcome_event_start)
        } else {
            activity.resources.getString(R.string.start)
        }
    )

    init {
        requestMyProfile()
    }

    private fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback{
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profileImage.set(userProfileResponse.profilePath)
                name.set("${userProfileResponse.name}님")
                userProfileResponse.megaOrganization?.let { organization ->
                    organization.name?.let { PreferenceManager.saveOrganizationName(it) }
                    userProfileResponse.megaDepartment?.let { department ->
                        department.name?.let { PreferenceManager.saveDepartmentName(it) }
                    }
                }
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(activity).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
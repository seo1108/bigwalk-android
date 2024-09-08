package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.event.EventActivity
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.extension.getDisplayEndDate
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.intValueToCommaString
import kr.co.bigwalk.app.util.showToast

class WelcomeEventViewModel(
    private val welcomeEventDialog: WelcomeEventDialog,
    private val activity: Activity,
    private val welcomeMission: MissionsResponse
) : BaseObservable() {
    val description: ObservableField<String> = ObservableField(
        String.format(
            activity.resources.getString(R.string.welcome_event_description),
            PreferenceManager.getName()
        )
    )
    val mission1Content: ObservableField<String> = ObservableField(
        String.format(
            activity.resources.getString(R.string.welcome_event_mission1_content),
            intValueToCommaString((welcomeMission.missions?.find { msn -> msn.sequence == 0 }?.mission?:"0").toInt())
        )
    )
    val mission2Content: ObservableField<String> = ObservableField(
        String.format(
            activity.resources.getString(R.string.welcome_event_mission2_content),
            (welcomeMission.missions?.find { msn -> msn.sequence == 1 }?.mission?:"0").toInt()
        )
    )
    val expiredDate: ObservableField<String> = ObservableField(
        getDisplayEndDate(welcomeMission.expiredDate, true)
    )

    fun goToEvent() {
        welcomeEventDialog.dismiss()
        activity.startActivity(Intent(activity, EventActivity::class.java))
    }

    /*init {
        requestMyProfile()
    }

    private fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback{
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                description.set(
                    String.format(
                        activity.resources.getString(R.string.welcome_event_description),
                        userProfileResponse.name
                    )
                )
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(activity).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }*/
}
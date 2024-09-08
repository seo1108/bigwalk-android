package kr.co.bigwalk.app.my_page.withdraw

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.exception.WithdrawUserException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.sensor.WalkService
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class WithdrawModel(
    private val activity: Activity?,
    private val navigator: BaseNavigator
) : BaseObservable(){

    val profile : ObservableField<UserProfileResponse> = ObservableField()

    init {
        requestMyProfile()
    }

    private fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback{
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profile.set(userProfileResponse)
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                profile.set(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }

    fun finishActivity() {
        navigator.finish()
    }

    fun withdrawUser() {
        UserRepository.withdrawUser(object : UserDataSource.WithdrawUserCallback {
            override fun onSuccess() {
                DebugLog.d("탈퇴 성공")
//                showToast("탈퇴가 완료 되었습니다.")
                PreferenceManager.saveAccessToken(null)
                PreferenceManager.saveDailyStep(0)
                PreferenceManager.saveDonableStep(0)
                PreferenceManager.saveTutorial(false)
                PreferenceManager.clearOrganization()

                // 웰컴 미션 초기화
                PreferenceManager.saveWelcomeMission1(-1)
                PreferenceManager.saveWelcomeMission1Max(0)
                PreferenceManager.saveWelcomeMission1Completed(false)
                PreferenceManager.saveWelcomeMission1ClearConfirmed(false)
                PreferenceManager.saveWelcomeMission2(-1)
                PreferenceManager.saveWelcomeMission2Max(0)
                PreferenceManager.saveWelcomeMission2Completed(false)
                PreferenceManager.saveWelcomeMission2ClearConfirmed(false)
                PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.NONE.type)
                AlarmBroadcastReceiver.cancelMissionAlarmManager(navigator.getContext())

                activity?.let {
                    startWalkService(it, 0, 0)
                }
                WalkService.stopService(navigator.getContext())
                val intent = Intent(activity, WithdrawCompleteActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                navigator.getContext().startActivity(intent)
            }

            override fun onFailed(reason: String) {
                DebugLog.e(WithdrawUserException(reason))
            }
        })
    }

    private fun startWalkService(context: Context, dailyStep: Int, donableStep: Int) {
        WalkServiceManager(context).syncStepToForeground(dailyStep, donableStep)
    }
}
package kr.co.bigwalk.app.event

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import gun0912.tedimagepicker.util.ToastUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.data.mission.dto.MissionsStatusResponse
import kr.co.bigwalk.app.data.mission.dto.RewardsResponse
import kr.co.bigwalk.app.data.mission.dto.WelcomeMissionCompleteRequest
import kr.co.bigwalk.app.data.mission.repository.MissionDataSource
import kr.co.bigwalk.app.data.mission.repository.MissionRepository
import kr.co.bigwalk.app.dialog.WelcomeResultDialog
import kr.co.bigwalk.app.extension.getDisplayEndDate
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.intValueToCommaString

class EventViewModel(
    private val navigator: BaseNavigator,
    private val firebaseAnalytics: FirebaseAnalytics
) : BaseObservable() {
    val mission1ContentText: ObservableField<String> = ObservableField("(로딩)걸음")
    val mission2ContentText: ObservableField<String> = ObservableField("(로딩)회")
    val missionDescription: ObservableField<String> = ObservableField("(로딩) 걸음 걷고\\n\n" +
            "최소 (로딩)회 기부 완료하면\\n\n" +
            "(로딩) 걸음을 드려요 :)")
    val missionExpiredDate: ObservableField<String> = ObservableField("~(로딩)")
    val isMission1Completed = MutableLiveData<Boolean>(false)
    val isMission2Completed = MutableLiveData<Boolean>(false)
    val isMissionCompleted = MutableLiveData<String>()
    var welcomeMissionReward: String? = null

    fun requestMissions() {
        MissionRepository.getMissions(object : MissionDataSource.MissionsCallback {
            override fun onSuccess(response: List<MissionsResponse>?) {
                response?.let {
                    if (it.count() <= 0) {
                        PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.NONE.type)
                        ToastUtil.showToast("웰컴 미션 기간이 종료되었습니다.")
                        navigator.finish()
                    }
                    it.find { msn -> msn.type == "welcome" }?.let { welcomeData ->
                        val welcomeMission1 = welcomeData.missions?.find { msn -> msn.sequence == 0 }
                        welcomeMission1?.let { mission1 -> PreferenceManager.saveWelcomeMission1Max(mission1.mission.toInt()) }
                        val welcomeMission1ContentText = welcomeMission1?.mission ?: "0"
                        val welcomeMission2 = welcomeData.missions?.find { msn -> msn.sequence == 1 }
                        welcomeMission2?.let { mission2 -> PreferenceManager.saveWelcomeMission2Max(mission2.mission.toInt()) }
                        val welcomeMission2ContentText = welcomeMission2?.mission ?: "0"
                        welcomeMissionReward = welcomeData.reward
                        mission1ContentText.set(
                            String.format(
                                navigator.getContext().resources.getString(R.string.my_mission_welcome_mission1_content),
                                intValueToCommaString(welcomeMission1ContentText.toInt())
                            )
                        )
                        mission2ContentText.set(
                            String.format(
                                navigator.getContext().resources.getString(R.string.my_mission_welcome_mission2_content),
                                welcomeMission2ContentText.toInt()
                            )
                        )
                        missionDescription.set(
                            String.format(
                                navigator.getContext().resources.getString(R.string.my_mission_description),
                                intValueToCommaString(welcomeMission1ContentText.toInt()),
                                welcomeMission2ContentText.toInt(),
                                intValueToCommaString(welcomeMissionReward?.toInt()?:0)
                            )
                        )
                        missionExpiredDate.set(
                            getDisplayEndDate(welcomeData.expiredDate, true)
                        )
                        PreferenceManager.saveWelcomeMissionStatus(welcomeData.status)
                        checkMissionStatus()
                    }
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.w("failed missions get")
            }
        })
    }

    private fun checkMissionStatus() {
        if (PreferenceManager.getWelcomeMission1Completed()) {
            isMission1Completed.postValue(true)
            if (!PreferenceManager.getWelcomeMission1ClearConfirmed()) {
                PreferenceManager.saveWelcomeMission1ClearConfirmed(true)
                requestWelcomeMissionComplete(
                    WelcomeMissionCompleteRequest(
                        0,
                        PreferenceManager.getWelcomeMission1().toString()
                    )
                )
            }
        }
        if (PreferenceManager.getWelcomeMission2Completed()) {
            isMission2Completed.postValue(true)
            if (!PreferenceManager.getWelcomeMission2ClearConfirmed()) {
                PreferenceManager.saveWelcomeMission2ClearConfirmed(true)
                requestWelcomeMissionComplete(
                    WelcomeMissionCompleteRequest(
                        1,
                        PreferenceManager.getWelcomeMission2().toString()
                    )
                )
            }
        }
        isMissionCompleted.postValue(PreferenceManager.getWelcomeMissionStatus())
    }

    fun moveToCampaign() {
        val intent = Intent(navigator.getContext(), CampaignActivity::class.java)
        navigator.getContext().startActivityForResult(intent, 1000)
        firebaseAnalytics.logEvent("all_campaign_button", Bundle())
    }

    fun requestRewards() {
        MissionRepository.postWelcomeRewards(object : MissionDataSource.RewardsCallback {
            override fun onSuccess(response: RewardsResponse) {
                if (response.status == "none") {
                    PreferenceManager.saveWelcomeMissionStatus(response.status)

                    if (welcomeMissionReward!=null) {
                        showWelcomeResultDialog(navigator.getContext(), welcomeMissionReward!!)
                    }
                }
            }

            override fun onFailed(code: Int?, reason: String?) {
                DebugLog.w("failed Rewards post")
                if (code == 406) {
                    showDialog(navigator.getContext().resources.getString(R.string.welcome_mission_reward_not_more_than))
                }
            }
        })
    }

    private fun showDialog(msg: String) {
        AlertDialog.Builder(navigator.getContext())
            .setMessage(msg)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                dialog.dismiss()
                moveToCampaign()
            }.create().show()
    }

    fun showWelcomeResultDialog(activity: Activity, welcomeMissionReward: String) {
        WelcomeResultDialog(activity, welcomeMissionReward).show()
    }

    fun finishActivity() {
        navigator.finish()
    }

    fun requestWelcomeMissionComplete(completeRequest: WelcomeMissionCompleteRequest) {
        MissionRepository.completeWelcomeMission(completeRequest,
            object : MissionDataSource.MissionsStatusCallback {
                override fun onSuccess(response: MissionsStatusResponse) {
                    PreferenceManager.saveWelcomeMissionStatus(response.status);
                    isMissionCompleted.postValue(PreferenceManager.getWelcomeMissionStatus())
                    DebugLog.w("success missions complete")
                }

                override fun onFailed(reason: String) {
                    DebugLog.w("failed missions complete")
                }
            })
    }
}
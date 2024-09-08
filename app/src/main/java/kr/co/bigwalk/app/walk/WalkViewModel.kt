package kr.co.bigwalk.app.walk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.all.AllActivity
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.community.CommunityInfoActivity
import kr.co.bigwalk.app.community.MyCommunityListActivity
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.data.mission.dto.MissionsStatusResponse
import kr.co.bigwalk.app.data.mission.repository.MissionDataSource
import kr.co.bigwalk.app.data.mission.repository.MissionRepository
import kr.co.bigwalk.app.data.user.dto.NoticeResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.WalkUtil
import kr.co.bigwalk.app.data.walk.repository.WalkDataSource
import kr.co.bigwalk.app.data.walk.repository.WalkRepository
import kr.co.bigwalk.app.dialog.NoticeDialogFragment
import kr.co.bigwalk.app.dialog.WelcomeEventDialog
import kr.co.bigwalk.app.event.EventActivity
import kr.co.bigwalk.app.my_page.MyPageModifyActivity
import kr.co.bigwalk.app.notification.RealtimeNotification
import kr.co.bigwalk.app.report.Report2Activity
import kr.co.bigwalk.app.share.ShareActivity
import kr.co.bigwalk.app.sign_in.organization.OrganizationFormActivity
import kr.co.bigwalk.app.space.SpaceGroupMemberFormActivity
import kr.co.bigwalk.app.util.*
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.WalkActivity.Companion.KEY_REQ_ORGANIZATION
import kr.co.bigwalk.app.walk.alarm.StepManager

interface DeepLinkHandleCallback {
    fun onError()
}

class WalkViewModel(
    private val navigator: WalkNavigator
) : BaseObservable() {
    val todayStep: ObservableField<String> =
        ObservableField(PreferenceManager.getDailyStep().toString())
    val donableStep: ObservableField<String> =
        ObservableField(PreferenceManager.getDonableStep().toString())
    val kcalText: ObservableField<String> =
        ObservableField("${PreferenceManager.getKcalorie()}")

    val donationRank: ObservableField<String> = ObservableField("-")
    val totalDonationStep: ObservableField<String> = ObservableField("0")
    val isNotification: ObservableField<Boolean> = RealtimeNotification.getInstance().notification
    val hasMissions: ObservableField<Boolean> = ObservableField(false)
    val missionStatusText: ObservableField<String> = ObservableField("미션 상태")
    var welcomeEventDialog: WelcomeEventDialog? = null
    val isMissionCompleted: ObservableField<Boolean> = ObservableField(false)
    val hasContestId = ObservableField<Long>(-1L)
    val hasRewardContestId = ObservableField<Long>(-1L)
    val showEvent = ObservableField<Boolean>(false)

    fun moveToShare() {
        val moveToShareIntent = Intent(navigator.getContext(), ShareActivity::class.java)
        navigator.getContext().startActivity(moveToShareIntent)
        WalkActivity.firebaseAnalytics?.logEvent("home_share_button", Bundle())
    }

    fun moveToGroup() {
        moveToCrew()
    }

    fun moveToCrew(invitedGroupId: Long? = null, key: String? = null) {
        val intent = MyCommunityListActivity.getIntent(navigator.getContext())
        invitedGroupId?.let { intent.putExtra(CommunityInfoActivity.PARM_INVITED_GROUP_ID, it) }
        key?.let { intent.putExtra(CommunityInfoActivity.PARM_INVITED_GROUP_KEY, it) }
        navigator.getContext().startActivity(intent)

        WalkActivity.firebaseAnalytics?.logEvent("home_group_button", Bundle())
    }

    fun moveToCampaign() {
        val intent = Intent(navigator.getContext(), CampaignActivity::class.java)
        navigator.getContext().startActivity(intent)
        WalkActivity.firebaseAnalytics?.logEvent("home_campaign_button", Bundle())
    }

    fun moveToCampaignDetail(campaignId: Long) {
        val intent = Intent(navigator.getContext(), CampaignActivity::class.java)
        intent.putExtra("campaign_id", campaignId)
        navigator.getContext().startActivity(intent)
        val bundle = Bundle()
        bundle.putString("campaign_id", campaignId.toString())
        WalkActivity.firebaseAnalytics?.logEvent("notification_campaign_detail", bundle)
    }

    fun moveToCrewCampaign(crewCampaignId: Long) {
        navigator.getContext().startActivity(CrewCampaignDetailActivity.getIntent(navigator.getContext(), crewCampaignId))
    }

    fun notifyStepChanged() {
        this.todayStep.set(intValueToCommaString(PreferenceManager.getDailyStep()))
        this.donableStep.set(intValueToCommaString(PreferenceManager.getDonableStep()))
        this.kcalText.set("${PreferenceManager.getKcalorie()}")
        this.navigator.setProgressPercentage(PreferenceManager.getDonableStepPercentage())
    }

    fun moveToRank() {
        val intent = Intent(navigator.getContext(), RankingPlusActivity::class.java)
        navigator.getContext().startActivity(intent)
        WalkActivity.firebaseAnalytics?.logEvent("home_ranking_button", Bundle())
    }

    fun moveToReport() {
        navigator.getContext().startActivity(Report2Activity.getIntent(navigator.getContext()))
        WalkActivity.firebaseAnalytics?.logEvent("home_report_button", Bundle())
    }

    fun moveToAll() {
        val moveToAll = Intent(navigator.getContext(), AllActivity::class.java)
        navigator.getContext().startActivity(moveToAll)
        WalkActivity.firebaseAnalytics?.logEvent("home_all_button", Bundle())
    }

    fun moveToSpaceBasedGroupChange(groupId: Long, campaignId: Long) {
        /*val intent = Intent(navigator.getContext(), SpaceGroupMemberFormActivity::class.java)
        intent.putExtra("deep_link_group_id", groupId)
        intent.putExtra("campaign_id", campaignId)
        navigator.getContext().startActivity(intent)*/

        /*val intent = Intent(navigator.getContext(), OrganizationFormActivity::class.java)
        intent.putExtra("CameFrom", "DeepLink")
        intent.putExtra("deep_link_group_id", groupId)
        intent.putExtra("campaign_id", campaignId)
        navigator.getContext().startActivityForResult(intent, WalkActivity.KEY_REQ_ORGANIZATION)*/

        val intent = Intent(navigator.getContext(), OrganizationFormActivity::class.java)
        intent.putExtra("CameFrom", "DeepLink")
        intent.putExtra("deep_link_group_id", groupId)
        intent.putExtra("campaign_id", campaignId)
        navigator.getContext().startActivity(intent)
    }

    fun moveBasedGroupChange(groupId: Long) {
        val intent = Intent(navigator.getContext(), OrganizationFormActivity::class.java)
        intent.putExtra("CameFrom", "DeepLink")
        intent.putExtra("deep_link_group_id", groupId)
        navigator.getContext().startActivity(intent)
    }


    fun requestNotice(supportFragmentManager: FragmentManager) {
        UserRepository.getNotice(object : UserDataSource.NoticeCallback {
            override fun onSuccess(noticeResponse: NoticeResponse) {
                if (!PreferenceManager.isNotShowingNotice(noticeResponse.id))
                    NoticeDialogFragment(noticeResponse.id, noticeResponse.title, noticeResponse.content).show(supportFragmentManager, "NoticeDialog")
            }

            override fun onFailed(reason: String) {
                DebugLog.w("failed notice get")
            }
        })
    }

    fun requestMissionStatus() {
        MissionRepository.getMissionsStatus(object : MissionDataSource.MissionsStatusCallback {
            override fun onSuccess(response: MissionsStatusResponse) {
                PreferenceManager.saveWelcomeMissionStatus(response.status)
                if (response.status != "none") {
                    setHasMission(true)
                    val statusText = if (response.status == "ongoing") {
                        navigator.getContext().resources.getString(R.string.mission_ongoing)
                    } else {
                        navigator.getContext().resources.getString(R.string.mission_completed)
                    }
                    missionStatusText.set(statusText)
                } else {
                    setHasMission(false)
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.w("failed missions status get")
            }
        })
    }

    // 튜토리얼 때만 웰컴미션 요청하는 함수
    fun requestMissions() {
        MissionRepository.getMissions(object : MissionDataSource.MissionsCallback {
            override fun onSuccess(response: List<MissionsResponse>?) {
                response?.let {
                    it.find { msn -> msn.type == "welcome" }?.let { welcomeData ->
                        PreferenceManager.saveWelcomeMissionStatus(welcomeData.status)
                        if (welcomeData.type != "none") {
                            welcomeData.missions?.let { missions ->
                                missions.find { msn -> msn.sequence == 0 }?.let { m1 ->
                                    PreferenceManager.saveWelcomeMission1(PreferenceManager.getDailyStep())
                                    PreferenceManager.saveWelcomeMission1Max(m1.mission.toInt())
                                    PreferenceManager.saveWelcomeMission1Completed(m1.clear)
                                }
                                missions.find { msn -> msn.sequence == 1 }?.let { m2 ->
                                    PreferenceManager.saveWelcomeMission2(0)
                                    PreferenceManager.saveWelcomeMission2Max(m2.mission.toInt())
                                    PreferenceManager.saveWelcomeMission2Completed(m2.clear)
                                }
                            }

                            welcomeEventDialog =
                                WelcomeEventDialog(navigator.getContext(), welcomeData)

                            AlarmBroadcastReceiver.startMissionAlarmManager(
                                navigator.getContext(),
                                welcomeData.expiredDate
                            )
                        }
                    }
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.w("failed missions get")
            }
        })
    }

    fun moveToEvent() {
        val moveToEvent = Intent(navigator.getContext(), EventActivity::class.java)
        navigator.getContext().startActivity(moveToEvent)
//        WalkActivity.firebaseAnalytics?.logEvent("home_event_button", Bundle())
    }

    fun showWelcomeEventDialog() {
        welcomeEventDialog?.show()
    }

    fun checkMissionCompleted() {
        val mission1 = PreferenceManager.getWelcomeMission1Completed() && !PreferenceManager.getWelcomeMission1ClearConfirmed()
        val mission2 = PreferenceManager.getWelcomeMission2Completed() && !PreferenceManager.getWelcomeMission2ClearConfirmed()
        isMissionCompleted.set(mission1 || mission2)
    }

    fun getOccupationInfo() {
        CrowdFundingRepository.getOccupationInfo(
            successCallback = { response ->
                response?.rewardCompetitionId?.let { setHasRewardContestId(it) }
                response?.competitionId?.let { setHasContentId(it) } // 위 line과 순서 보장해야 함 (competitionId, rewareCompetitionId 둘 다 나왔을 경우 competitionId 높은 우선순위)
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun handleDeepLink(activity: Activity, callback: DeepLinkHandleCallback) {
        DeepLinkStorage(activity).loadAndClear(object : DeepLinkStorageCallback {
            override fun onLoad(deepLinkData: DeepLinkData) {
                when (deepLinkData.deepLinkType) {
                    DeepLinkType.CAMPAIGN_DETAIL -> {
                        if (deepLinkData.campaignId <= DeepLinkData.ID_NULL) {
                            callback.onError()
                            return
                        }
                        moveToCampaignDetail(deepLinkData.campaignId)
                    }
                    DeepLinkType.SPACE_GROUP -> {
                        if (deepLinkData.campaignId <= DeepLinkData.ID_NULL) {
                            callback.onError()
                            return
                        }
                        if (PreferenceManager.getOrganization() == deepLinkData.groupId) {
                            moveToCampaignDetail(deepLinkData.campaignId)
                        } else {
                            moveToSpaceBasedGroupChange(deepLinkData.groupId, deepLinkData.campaignId)
                        }
                    }
                    DeepLinkType.GROUP_JOIN -> {
                        if (deepLinkData.groupId <= DeepLinkData.ID_NULL) {
                            callback.onError()
                            return
                        }
                        deepLinkData.data?.let { key ->
                            //moveToCrew(deepLinkData.groupId, key)
                            moveBasedGroupChange(deepLinkData.groupId)
                        }
                    }
                    DeepLinkType.CREW_CAMPAIGN -> {
                        if (deepLinkData.crewCampaignId <= DeepLinkData.ID_NULL) {
                            callback.onError()
                            return
                        }
                        moveToCrewCampaign(deepLinkData.crewCampaignId)
                    }
                    DeepLinkType.CREW_JOIN -> {
                        if (deepLinkData.groupId <= DeepLinkData.ID_NULL) {
                            callback.onError()
                            return
                        }
                        moveToCrew(deepLinkData.groupId, deepLinkData.data)
                    }
                    else -> {
                    }
                }
            }
        })
    }

    private fun setHasMission(value: Boolean) {
        hasMissions.set(value)
        refreshEventView()
    }

    private fun setHasContentId(value: Long) {
        hasContestId.set(value)
        hasRewardContestId.set(-1)
        refreshEventView()
    }

    private fun setHasRewardContestId(value: Long) {
        hasRewardContestId.set(value)
        hasContestId.set(-1)
        refreshEventView()
    }

    private fun refreshEventView() {
        showEvent.set(false)
        hasMissions.get()?.let { if (it) showEvent.set(true) }
        hasContestId.get()?.let { if (it > 0) showEvent.set(true) }
        hasRewardContestId.get()?.let { if (it > 0) showEvent.set(true) }
    }
}
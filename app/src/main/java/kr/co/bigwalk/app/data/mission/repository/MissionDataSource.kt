package kr.co.bigwalk.app.data.mission.repository

import kr.co.bigwalk.app.data.mission.dto.*

interface MissionDataSource {
    interface MissionsCallback {
        fun onSuccess(response: List<MissionsResponse>?)
        fun onFailed(reason: String)
    }
    interface RewardsCallback {
        fun onSuccess(response: RewardsResponse)
        fun onFailed(code:Int?, reason: String?)
    }
    interface MissionsStatusCallback {
        fun onSuccess(response: MissionsStatusResponse)
        fun onFailed(reason: String)
    }

    fun getMissions(missionsCallback: MissionsCallback)
    fun postWelcomeRewards(rewardsCallback: RewardsCallback)
    fun getMissionsStatus(missionsStatusCallback: MissionsStatusCallback)
    fun completeWelcomeMission(completeRequest: WelcomeMissionCompleteRequest, missionsStatusCallback: MissionsStatusCallback)
}
package kr.co.bigwalk.app.data.mission.repository

import kr.co.bigwalk.app.data.mission.dto.WelcomeMissionCompleteRequest

object MissionRepository: MissionDataSource {
    override fun getMissions(missionsCallback: MissionDataSource.MissionsCallback) {
        MissionRemoteDataSource.getMissions(missionsCallback)
    }

    override fun postWelcomeRewards(rewardsCallback: MissionDataSource.RewardsCallback) {
        MissionRemoteDataSource.postWelcomeRewards(rewardsCallback)
    }

    override fun getMissionsStatus(missionsStatusCallback: MissionDataSource.MissionsStatusCallback) {
        MissionRemoteDataSource.getMissionsStatus(missionsStatusCallback)
    }

    override fun completeWelcomeMission(
        completeRequest: WelcomeMissionCompleteRequest,
        missionsStatusCallback: MissionDataSource.MissionsStatusCallback
    ) {
        MissionRemoteDataSource.completeWelcomeMission(completeRequest, missionsStatusCallback)
    }
}
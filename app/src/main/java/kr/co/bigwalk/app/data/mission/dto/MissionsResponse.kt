package kr.co.bigwalk.app.data.mission.dto

import kr.co.bigwalk.app.data.mission.Mission

data class MissionsResponse(
    val type: String,
    val status: String,
    val expiredDate: String,
    val description: String,
    val reward: String,
    val missions: List<Mission>?
)

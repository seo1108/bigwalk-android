package kr.co.bigwalk.app.data.feedHome.dto

data class ChallengeHomeResponse(
    val actives: List<ChallengeInfoResponse>,
    val participations: List<ChallengeInfoResponse>,
    val closed: List<ChallengeInfoResponse>
)

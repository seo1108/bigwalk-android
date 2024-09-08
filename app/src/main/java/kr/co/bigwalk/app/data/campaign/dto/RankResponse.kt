package kr.co.bigwalk.app.data.campaign.dto

data class RankResponse(
    val startDate: String,
    val endDate: String,
    val season: String,
    val my: RankData,
    val ranking: List<RankData>
)

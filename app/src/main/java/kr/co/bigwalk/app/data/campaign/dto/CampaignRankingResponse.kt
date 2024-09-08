package kr.co.bigwalk.app.data.campaign.dto

data class CampaignRankingResponse(
    val userId: Long,
    val profilePath: String,
    val name: String,
    val rank: Long?
)
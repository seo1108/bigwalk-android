package kr.co.bigwalk.app.data.feedHome.dto

data class MissionCampaignResponse(
    val missionId: Int,
    val campaignId: Int,
    val title: String,
    val content: String,
    val mainImagePath: String,
    val groupName: String? = "",
    val organizationId: Int,
    val isOpen: Boolean,
    val startDate: String,
    val endDate: String
)

package kr.co.bigwalk.app.data.campaign.dto

data class CampaignContentResponse (
    val id: Long,
    val title: String,
    val description: String,
    val campaignContentType: CampaignContentType,
    val contentImages: List<CampaignContentImageResponse>,
    val videoPath: String
)
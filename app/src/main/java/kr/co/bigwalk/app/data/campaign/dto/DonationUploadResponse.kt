package kr.co.bigwalk.app.data.campaign.dto

data class DonationUploadResponse(
    val missionId: Long,
    val campaignId: Long,
    val title: String,
    val content: String,
    val mainImagePath: String,
    val organizationId: Long,
    val startDate: String,
    val endDate: String
)
package kr.co.bigwalk.app.data.campaign.dto

data class CampaignSMSContentResponse (
    val id: Long,
    val title: String,
    val number: String,
    val text: String,
    val amount: Long,
    val heading: String,
    val content: String,
    val privacyPolicy: String
)
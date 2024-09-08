package kr.co.bigwalk.app.data.campaign.dto

data class ActionMissionResponse(
    val id: Long,
    val title: String,
    val content: String,
    val point: Int,
    val firstHowToImagePath: String,
    val secondHowToImagePath: String,
    val firstInvalidImagePath: String,
    val secondInvalidImagePath: String,
    val firstHowToDescription: String,
    val secondHowToDescription: String,
    val firstInvalidDescription: String,
    val secondInvalidDescription: String,
    val startDate: String,
    val endDate: String
)
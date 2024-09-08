package kr.co.bigwalk.app.data.report.dto

data class MinResponse(
    val dailyDate: String,
    val dailyDonationSteps: Int,
    val dailySteps: Int,
    val dayOfWeek: String
)
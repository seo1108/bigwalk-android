package kr.co.bigwalk.app.data.report.dto

data class MaxResponse(
    val dailyDate: String,
    val dailyDonationSteps: Int,
    val dailySteps: Int,
    val dayOfWeek: String
)
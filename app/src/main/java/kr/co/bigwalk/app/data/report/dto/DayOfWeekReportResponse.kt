package kr.co.bigwalk.app.data.report.dto

import java.util.*

data class DayOfWeekReportResponse(
    val averageDonationSteps: Int,
    val caloriesBurned: Int,
    val distanceWalked: Int,
    val max: MaxResponse,
    val min: MinResponse,
    val reportWalks: List<ReportWalkResponse>,
    val targetStep: Int,
    val totalDonationStep: Int,
    val totalStep: Int,
    val existPrevious: Boolean
) {
    fun getWeekOfMonth(): String {
        val c = Calendar.getInstance()
        c.minimalDaysInFirstWeek = 3

        val thursdayOfWeek: ReportWalkResponse? = reportWalks.find { it.dayOfWeek == DayOfWeek.THURSDAY }
        val dateArray = thursdayOfWeek?.dailyDate?.split('-').orEmpty()
        c.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())

        val weekOfMonth = c.get(Calendar.WEEK_OF_MONTH)

        return "${dateArray[0].slice(2..3)}년 ${dateArray[1]}월 ${weekOfMonth}주차"
    }

    fun getRangeOfWeek(): String {
        val firstOfWeek = reportWalks.first().dailyDate.replace("-", "/")
        val lastOfWeek = reportWalks.last().dailyDate.replace("-", "/")
        return "$firstOfWeek ~ $lastOfWeek"
    }
}
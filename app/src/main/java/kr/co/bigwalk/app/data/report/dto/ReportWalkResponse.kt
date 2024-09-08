package kr.co.bigwalk.app.data.report.dto

import java.util.*

data class ReportWalkResponse(
    val dailyDate: String,
    val dailyDonationSteps: Int,
    val dailySteps: Int,
    val dayOfWeek: DayOfWeek
) {
    fun dateToTimeInMillis(): Long {
        val c = Calendar.getInstance()

        val dateArray = dailyDate.split('-')
        c.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
        return c.timeInMillis
    }
}
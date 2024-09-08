package kr.co.bigwalk.app.data.report.dto

data class DayOfMonthReportResponse(
    val averageDonationSteps: Long,
    val caloriesBurned: Long,
    val distanceWalked: Long,
    val max: MaxResponse?,
    val min: MinResponse?,
    val reportWalks: List<ReportWalkResponse>?,
    val targetStep: Long,
    val totalDonationStep: Long,
    val totalStep: Long,
    val existPrevious: Boolean
) {
    fun getMonth(): String {
        val day: ReportWalkResponse? = reportWalks?.first()
        val dateArray = day?.dailyDate?.split('-').orEmpty()

        return "${dateArray[0].slice(2..3)}년 ${dateArray[1]}월"
    }
}
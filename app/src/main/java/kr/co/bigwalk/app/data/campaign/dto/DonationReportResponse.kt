package kr.co.bigwalk.app.data.campaign.dto

data class DonationReportResponse(
    val donatedTimes: Int,
    val donatedSteps: Long
) {
    fun getDonatedTimesString(): String {
        return "현재까지 기부한 횟수 $donatedTimes 번 입니다!"
    }

    fun getDonatedStepsString(): String {
        return "현재까지 기부한 총 걸음수 $donatedSteps"
    }
}
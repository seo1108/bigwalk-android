package kr.co.bigwalk.app.data.share

import kr.co.bigwalk.app.util.longValueToCommaString

data class ResponseShare (
    val name: String,
    val todayDonatedSteps: Long,
    val totalDonatedSteps: Long,
    val rank: Long
) {

    fun getTodayDonatedStepsString(): String {
        val steps = if(todayDonatedSteps > 10000) {
            longValueToCommaString(todayDonatedSteps / 10000) + "만 " + todayDonatedSteps % 10000
        } else {
            longValueToCommaString(todayDonatedSteps)
        }
        return "${name}님은 오늘\n${steps}걸음을 기부했습니다"
    }

    fun getTotalDonatedStepsString(): String {
        val steps = if(totalDonatedSteps > 10000) {
            longValueToCommaString(totalDonatedSteps / 10000) + "만 " + totalDonatedSteps % 10000
        } else {
            longValueToCommaString(totalDonatedSteps)
        }
        return "${steps}걸음"
    }

    fun getTotalDonatedStepsCommaString(): String {
        return "${longValueToCommaString(totalDonatedSteps)}걸음"
    }

    fun getRankString(): String {
        val rank = if (rank==0L) "-" else longValueToCommaString(rank)
        return "${rank}위"
    }

}
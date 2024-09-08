package kr.co.bigwalk.app.data.campaign.dto

import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.longValueToCommaString

data class RankData(
    val rank: Rank,
    val number: Long,
    val name: String,
    val profilePath: String,
    val donatedSteps: Long,
    val currentRankSteps: Long,
    val nextRankSteps: Long,
    val mine: Boolean,
    val report: MyReport?
) {
    fun getTotalDonatedSteps(): String {
        return if(donatedSteps > 10000) {
            longValueToCommaString(donatedSteps / 10000) + "만" + longValueToCommaString(donatedSteps % 10000)
        } else {
            longValueToCommaString(donatedSteps)
        }
    }

    fun getProgress(): Int {
        return ((donatedSteps - currentRankSteps).toFloat() / (nextRankSteps - currentRankSteps) * 100).toInt()
    }

    fun getRankNumber(): String {
        if (number==-1L || number==0L) {
            return "-"
        }
        return longValueToCommaString(number)
    }
    fun getCurrentRankStepsText() = longValueToCommaString(currentRankSteps)
    fun getNextRankStepsText() = longValueToCommaString(nextRankSteps)

    fun getRankString(): String {
        return "${getRankNumber()}위 / ${rank.name}"
    }
}

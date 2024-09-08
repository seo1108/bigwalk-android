package kr.co.bigwalk.app.data.campaign.dto

import kr.co.bigwalk.app.extension.valueToCommaString
import java.text.SimpleDateFormat
import java.util.*

data class DonationInfoResponse(
    val donatedSteps: Int,
    var todayDonatedSteps: Long,
    val ranking: Int,
    val lastDonatedDateTime: Date?,
    val story: Boolean
) {
    fun getDonatedStepsString(): String {
        return donatedSteps.valueToCommaString() + "걸음 기부"
    }

    fun getRankString(): String {
        return ranking.valueToCommaString() + "위"
    }

    fun getLastDonatedTimeString(): String {
        return if (lastDonatedDateTime != null) {
            val simpleDateFormat = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
            simpleDateFormat.format(lastDonatedDateTime)
        } else {
            ""
        }
    }
}
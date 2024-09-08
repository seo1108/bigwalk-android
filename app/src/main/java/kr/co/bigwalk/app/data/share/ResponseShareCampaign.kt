package kr.co.bigwalk.app.data.share

import kr.co.bigwalk.app.util.longValueToCommaString
import java.text.SimpleDateFormat
import java.util.*

data class ResponseShareCampaign(
    val campaignName: String,
    val startDate: Date,
    val endDate: Date,
    val donationRatio: Long,
    val donatedSteps: Long,
    val name: String,
    val rank: Long,
    val imagePath: String
) {

    fun getPeriodString(): String {
        return SimpleDateFormat(
            "yyyy.MM.dd",
            Locale.KOREA
        ).format(startDate) + "~" + SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(endDate)
    }

    fun getDonatedStepsString(): String {
        val steps = if (donatedSteps > 10000) {
            longValueToCommaString(donatedSteps / 10000) + "만 " + donatedSteps % 10000
        } else {
            longValueToCommaString(donatedSteps)
        }
        return "${name}님은 이 캠페인에\n${steps}걸음을 기부했습니다"
    }

    fun getDonationRatioString(): String {
        return "${donationRatio}%"
    }

    fun getRankString(): String {
        return longValueToCommaString(rank) + "위"
    }

}
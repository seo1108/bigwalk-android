package kr.co.bigwalk.app.data.report.dto

import kr.co.bigwalk.app.util.longValueToCommaString

data class MonthStatsResponse(
    val totalParticipatedCampaignCount: Long,
    val totalDonationCount: Long,
    val totalSteps: Long,
    val ordinalWeekStats: List<OrdinalWeekStatsResponse>,
    val totalCalories: Long,
    val overTenThousandStepsDaysCount: Long,
    val maxAvgCalories: Long,
    val minAvgCalories: Long,
    val maxDonationCount: Long,
    val minDonationCount: Long,
    val maxDonationSteps: Long,
    val minDonationSteps: Long,
    val maxAvgCaloriesWeekOrder: List<OrdinalWeekStatsResponse>,
    val minAvgCaloriesWeekOrder: List<OrdinalWeekStatsResponse>,
    val maxDonationCountWeekOrder: List<OrdinalWeekStatsResponse>,
    val minDonationCountWeekOrder: List<OrdinalWeekStatsResponse>,
    val maxDonationStepsWeekOrder: List<OrdinalWeekStatsResponse>,
    val minDonationStepsWeekOrder: List<OrdinalWeekStatsResponse>,
    val days: List<DayStatsForMonthResponse>
) {
    fun totalParticipatedCampaignCountString(): String {
        return "이번 달에는 ${totalParticipatedCampaignCount}개 캠페인에\n참여했어요"
    }

    fun totlaDonationCountString(): String {
        return "이번 달에는 ${totalDonationCount}번\n기부했어요"
    }

    fun totalStepsString(): String {
        val firstValue = totalSteps / 10000
        val secondValue = totalSteps % 10000
        val totalString = if (firstValue > 0) {
            "${longValueToCommaString(firstValue)}만 $secondValue"
        } else {
            longValueToCommaString(secondValue)
        }
        return "이번 달에는 총 $totalString 걸음을\n걸었어요"
    }

    fun totalCaloriesString(): String {
        val firstValue = totalCalories / 10000
        val secondValue = totalCalories % 10000
        val totalString = if (firstValue > 0) {
            "${longValueToCommaString(firstValue)}만 $secondValue"
        } else {
            longValueToCommaString(secondValue)
        }
        return "이번 달 소모한 칼로리는\n총 ${totalString}kcal 입니다"
    }

    fun maxCaloriesWeekString(): String {
        return maxAvgCaloriesWeekOrder[0].getWeekOrder()
    }

    fun minCaloriesWeekString(): String {
        return minAvgCaloriesWeekOrder[0].getWeekOrder()
    }

    fun maxCaloriesString(): String {
        return "${maxAvgCaloriesWeekOrder[0].totalCalories}"
    }

    fun minCaloriesString(): String {
        return "${minAvgCaloriesWeekOrder[0].totalCalories}"
    }

}
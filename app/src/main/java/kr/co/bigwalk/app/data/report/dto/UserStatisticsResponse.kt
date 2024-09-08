package kr.co.bigwalk.app.data.report.dto

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import kr.co.bigwalk.app.util.longValueToCommaString
import java.text.SimpleDateFormat
import java.util.*

data class UserStatisticsResponse(
    val walkStatistics: List<WalkStatistics>,
    val average: Long,
    val min: WalkStatistics,
    val max: WalkStatistics
) {

    fun getDateOfWeek(): String {
        val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        return formatter.format(walkStatistics[0].dailyDate) + "~" + formatter.format(walkStatistics[walkStatistics.size - 1].dailyDate)
    }

    fun getAverageStepsString(): SpannableString {
        return setSpannableString("최근 일주일\n평균 ${longValueToCommaString(average)} 걸음 입니다.")
    }

    fun getTotalDonatedStepsString(): SpannableString {
        var totalDonatedSteps = 0L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            totalDonatedSteps = walkStatistics.stream().mapToLong(WalkStatistics::dailySteps).sum()
        } else {
            for (statistics in walkStatistics) {
                totalDonatedSteps += statistics.dailySteps
            }
        }
        return setSpannableString("최근 일주일 기부한 걸음 수는\n총 ${longValueToCommaString(totalDonatedSteps)} 걸음 입니다.")
    }

    fun getTheMostDayString(): String {
        return "${max.getDayOfWeekString()}요일"
    }

    fun getTheLeastDayString(): String {
        return "${min.getDayOfWeekString()}요일"
    }

    private fun setSpannableString(str: String): SpannableString {
        val spannable = SpannableString(str)
        val startIndex = str.indexOfFirst { it.isDigit() }
        val lastIndex = str.indexOfLast { it.isDigit() }
        spannable.setSpan(StyleSpan(Typeface.BOLD)
                            , startIndex
                            , lastIndex + 1
                            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#4379e7"))
                            , startIndex
                            , lastIndex + 1
                            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

}
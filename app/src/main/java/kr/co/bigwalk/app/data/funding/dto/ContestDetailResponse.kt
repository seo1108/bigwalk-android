package kr.co.bigwalk.app.data.funding.dto

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class ContestDetailResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("logoImagePath")
    val logoImagePath: String?,
    @SerializedName("mainColor")
    private val mainColor: String?,
    @SerializedName("fontColor")
    private val fontColor: String?,
    @SerializedName("startDate")
    val startDate: Date,
    @SerializedName("endDate")
    val endDate: Date,
    @SerializedName("campaignStartDate")
    val campaignStartDate: Date?,
    @SerializedName("campaignEndDate")
    val campaignEndDate: Date?
) {
    fun getMainColor(): Int {
        return if (mainColor.isNullOrEmpty()) {
            Color.LTGRAY
        } else {
            Color.parseColor(mainColor)
        }
    }

    fun getFontColor(): Int {
        return if (fontColor.isNullOrEmpty()) {
            Color.LTGRAY
        } else {
            Color.parseColor(fontColor)
        }
    }

    fun getScheduledOpenDate(): String {
        if (campaignStartDate != null && campaignEndDate != null) {
            val startDate = SimpleDateFormat("yy.MM.dd").format(campaignStartDate)
            val endDate = SimpleDateFormat("MM.dd").format(campaignEndDate)
            return "캠페인 오픈 예정 $startDate~$endDate"
        }

        return ""
    }

    fun getRecruitmentPeriod(): String {
        val startDate = SimpleDateFormat("yy.MM.dd").format(startDate)
        val endDate = SimpleDateFormat("MM.dd").format(endDate)

        return "$startDate~$endDate"
    }
}

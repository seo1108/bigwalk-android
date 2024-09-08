package kr.co.bigwalk.app.data.funding.dto

import android.graphics.Color
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class ContestsResponse(
    @SerializedName("data")
    val data: List<ContestResponse>
)

data class ContestResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String?,
    @SerializedName("mainImagePath")
    val mainImagePath: String?,
    @SerializedName("startDate")
    val startDate: Date,
    @SerializedName("endDate")
    val endDate: Date,
    @SerializedName("fontColor")
    private val fontColor: String?,
    @SerializedName("logoImagePath")
    val logoImagePath: String,
    @SerializedName("mainColor")
    private val mainColor: String?
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

    fun getRecruitmentPeriod(): String {
        val startDate = SimpleDateFormat("yy.MM.dd").format(startDate)
        val endDate = SimpleDateFormat("MM.dd").format(endDate)

        return "$startDate~$endDate"
    }
}
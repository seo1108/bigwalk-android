package kr.co.bigwalk.app.data.crowd_funding.dto

import android.graphics.Color
import com.google.gson.annotations.SerializedName

data class ContestPostersResponse(
    @SerializedName("posterImage")
    val posterImagePath: String,
    @SerializedName("detailImage")
    val detailImagePath: String,
    @SerializedName("fontColor")
    private val fontColor: String?,
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
}

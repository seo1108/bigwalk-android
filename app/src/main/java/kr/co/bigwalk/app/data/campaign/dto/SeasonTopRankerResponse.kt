package kr.co.bigwalk.app.data.campaign.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.extension.valueToCommaString

data class SeasonTopRankerResponse(
    @SerializedName("seasonKey")
    val seasonKey: String,
    @SerializedName("rank")
    val rank: SeasonRankResponse,
    @SerializedName("number")
    val number: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("profilePath")
    val profilePath: String,
    @SerializedName("donatedSteps")
    val donatedSteps: Int
) {
    fun getSeason(): String {
        return if (seasonKey.last() == '0'){
            "프리시즌"
        } else {
            "시즌"+seasonKey.last().toString()
        }
    }

    fun getDonatedStepsString(): String {
        return if (donatedSteps > 10000) {
            (donatedSteps / 10000).valueToCommaString() + "만 " + donatedSteps % 10000
        } else {
            (donatedSteps / 10000).valueToCommaString()
        }
    }
}

data class SeasonRankResponse(
    @SerializedName("level")
    val level: Int,
    @SerializedName("name")
    val name: String
)

package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.community.share.StickerItem
import kr.co.bigwalk.app.extension.ellipsizeByMaxLength
import kr.co.bigwalk.app.util.longValueToCommaString
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class GroupShareResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("mainImagePath")
    val mainImagePath: String,
    @SerializedName("goalPoint")
    private val goalPoint: Long,
    @SerializedName("myDonatedStep")
    private val myDonatedStep: Long,
    @SerializedName("ratio")
    val ratio: Int,
    @SerializedName("propensity")
    val propensity: PropensityType,
    @SerializedName("report")
    val report: ShareReportResponse,
    @SerializedName("stickers")
    val stickers: ArrayList<StickerResponse>,
    @SerializedName("startDate")
    val startDate: Date,
    @SerializedName("endDate")
    val endDate: Date
) : Serializable {
    fun getMaxName(): String {
        return name.ellipsizeByMaxLength(GROUP_NAME_MAX_LENGTH)
    }
    
    fun getGoalPoint(): String =
        longValueToCommaString(goalPoint)
    
    fun getMyDonatedStep(): String =
        longValueToCommaString(myDonatedStep)
    
    fun getThisWeekDate(): String {
        val startDate = SimpleDateFormat("yy.MM.dd").format(this.startDate)
        val endDate = SimpleDateFormat("yy.MM.dd").format(this.endDate)
        return "$startDate~$endDate"
    }
    
    companion object {
        const val GROUP_NAME_MAX_LENGTH = 10
    }
}

data class ShareReportResponse(
    @SerializedName("donatedSteps")
    private val donatedSteps: Long,
    @SerializedName("reducedCarbonAmount")
    private val reducedCarbonAmount: Long,
    @SerializedName("plantingAmount")
    private val plantingAmount: Long
) : Serializable {
    fun donatedSteps(): String =
        longValueToCommaString(donatedSteps)
    
    fun getReducedCarbonAmount(): String =
        longValueToCommaString(reducedCarbonAmount)
    
    fun getPlantingAmount(): String =
        longValueToCommaString(plantingAmount)
}

data class StickerResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("imagePath")
    val imagePath: String,
    @SerializedName("expiredDate")
    val expiredDate: Date? = null,
    @SerializedName("type")
    val type: StickerType
) : Serializable {
    fun toStickerItem(oldId: ArrayList<Int>): StickerItem {
        return StickerItem(
            id = id,
            name = name,
            imagePath = imagePath,
            expiredDate = expiredDate,
            type = type,
            new = !oldId.contains(id)
        )
    }
}

enum class StickerType {
    BASIC,
    USER,
    GROUP
}
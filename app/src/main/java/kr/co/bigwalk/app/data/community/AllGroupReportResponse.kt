package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.util.longValueToCommaString

data class AllGroupReportResponse(
    @SerializedName("totalDonatedSteps")
    private val totalDonatedSteps: Long,
    @SerializedName("reducedCarbonAmount")
    private val reducedCarbonAmount: Long,
    @SerializedName("plantingAmount")
    private val plantingAmount: Long
) {
    fun getTotalDonatedSteps(): String =
        longValueToCommaString(totalDonatedSteps)
    
    fun getReducedCarbonAmount(): String =
        longValueToCommaString(reducedCarbonAmount)
    
    fun getPlantingAmount(): String =
        longValueToCommaString(plantingAmount)
    
}
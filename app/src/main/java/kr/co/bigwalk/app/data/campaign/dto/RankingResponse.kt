package kr.co.bigwalk.app.data.campaign.dto

import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.longValueToCommaString

data class RankingResponse(
    val userId: Long,
    val profilePath: String,
    val name: String,
    val rank: Long?,
    val donatedSteps: Long,
    var departmentName: String? = null
) {
    fun getDonatedStepsString(): String {
        return if (donatedSteps > 10000) {
            (donatedSteps / 10000).valueToCommaString() + "만 " + donatedSteps % 10000
        } else {
            donatedSteps.valueToCommaString()
        }
    }

    fun getRankString(): String {
        val rankText = if (rank==null || rank==0L) "-" else rank.toString()
        return "${rankText}위"
    }

    fun getTotalDonatedSteps(): String {
        return if(donatedSteps > 10000) {
            longValueToCommaString(donatedSteps / 10000) + "만"
        } else {
            longValueToCommaString(donatedSteps)
        }
    }

    fun showDepartmentName(): Boolean {
//        return departmentName!=null
        return false
    }
}
package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.organization.GroupType

data class GroupSummaryInfoResponse(
    val name: String,
    val description: String,
    val mainImagePath: String,
    val type: GroupType,
    val propensity: PropensityType
)

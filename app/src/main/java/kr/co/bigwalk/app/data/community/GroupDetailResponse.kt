package kr.co.bigwalk.app.data.community

import kr.co.bigwalk.app.data.organization.GroupType
import kr.co.bigwalk.app.extension.ellipsizeByMaxLength
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.longValueToCommaString
import java.util.*

enum class PropensityType {
    HEALTH,
    ENVIRONMENT,
    RUNNING,
    NONE
}

enum class GoalType {
    STEP
}

data class GroupDetailResponse(
    val id: Long,
    val name: String,
    val description: String,
    val mainImagePath: String?,
    val propensity: PropensityType,
    val type: GroupType,
    val count: Int,
    val isOwner: Boolean,
    val target: TargetResponse,
    val report: ReportResponse,
    val rank: GroupRankResponse?,
    val joinRequestCounts: Int
) {
    fun getMaxLengthName(): String {
        return name.ellipsizeByMaxLength(GroupShareResponse.GROUP_NAME_MAX_LENGTH)
    }
    fun isShowRequestAlarm(): Boolean {
        if (isOwner) {
            return joinRequestCounts > 0
        } else {
            return false
        }
    }
}

data class TargetResponse(
    val goalPoint: Long,
    val goalType: GoalType,
    val ratio: Int,
    val maxPoint: Int,
    val endDate: Date
) {
    companion object {
        const val GOAL_POINT_UNIT = 10000
    }
    fun goalPointToCommaString(): String {
        return "${goalPoint.valueToCommaString(GOAL_POINT_UNIT)}만 "
    }
    fun maxPointToCommaString(): String {
        return "${maxPoint.valueToCommaString(GOAL_POINT_UNIT)}만"
    }
}

data class ReportResponse(
    private val totalDonatedSteps: Long,
    private val reducedCarbonAmount: Long,
    private val plantingAmount: Long,
    val message: String
) {
    fun getTotalDonatedSteps(): String =
        totalDonatedSteps.valueToCommaString()

    fun getReducedCarbonAmount(): String =
        reducedCarbonAmount.valueToCommaString()

    fun getPlantingAmount(): String =
        plantingAmount.valueToCommaString()

}

data class UsersResponse(
    val id: Int,
    val name: String,
    val profilePath: String,
    val role: GroupMemberRole // enum memberType
)

data class GroupRankResponse(
    val level: Long,
    private val nextGapPoint: Long,
    val participantsCount: Int
) {
    fun getNextGapPoint(): String {
        return if (nextGapPoint > 10000) {
            longValueToCommaString(nextGapPoint / 10000) + "만 " + if (nextGapPoint % 10000 != 0L) nextGapPoint % 10000 else ""
        } else {
            longValueToCommaString(nextGapPoint)
        }
    }
}
